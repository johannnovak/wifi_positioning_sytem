#include "pcap-thread.h"

extern volatile sig_atomic_t got_sigint;
extern Element * rssi_list;
extern sem_t synchro;

void *pcap_function(void *arg)
{
	char *iface = (char *) arg;
	char errbuf[PCAP_ERRBUF_SIZE];
	pcap_t * handle = NULL;
	struct ieee80211_radiotap_header * rtap_head;
	struct ieee80211_header * eh;
	struct pcap_pkthdr header;
	const u_char * packet;
	u_char * mac;
	u_char first_flags;
	int offset = 0;
	char rssi;
	Element * dev_info;
	
	u_char alc_mac[6];
	string_to_mac("00:73:8d:9e:cb:ab", alc_mac);
	
	// Open pcap handle to sniff traffic
	handle = pcap_open_live(iface, BUFSIZ, 1, 1000, errbuf);
	if (handle == NULL) {
		printf("Could not open pcap on %s\n", iface);
		pthread_exit((void *) -1);
	}

	while (got_sigint == 0) {
		packet = pcap_next(handle, &header);
		if (!packet)
			continue;

		rtap_head = (struct ieee80211_radiotap_header *) packet;
		int len = (int) rtap_head->it_len[0] + 256 * (int) rtap_head->it_len[1];
		eh = (struct ieee80211_header *) (packet + len);
		if ((eh->frame_control & 0x03) == 0x01) {
			mac = eh->source_addr;
			first_flags = rtap_head->it_present[0];
			offset = 8;
			offset += ((first_flags & 0x01) == 0x01) ? 8 : 0 ;
			offset += ((first_flags & 0x02) == 0x02) ? 1 : 0 ;
			offset += ((first_flags & 0x04) == 0x04) ? 1 : 0 ;
			offset += ((first_flags & 0x08) == 0x08) ? 4 : 0 ;
			offset += ((first_flags & 0x10) == 0x10) ? 2 : 0 ;
			rssi = *((char *) rtap_head + offset) - 0x100;
			if(rssi == 4)
				continue;
				
			printf("%d bytes -- %02X:%02X:%02X:%02X:%02X:%02X -- RSSI: %d dBm\n",
			       len, mac[0], mac[1], mac[2], mac[3], mac[4], mac[5], (int) rssi);
			// We got some message issued by a terminal (FromDS=0,ToDS=1)
			sem_wait(&synchro);
			if ((dev_info = find_mac(rssi_list, mac)) == NULL) {
				dev_info = add_element(&rssi_list, mac);
			}
			clear_outdated_values(&dev_info->measurements);
			add_value(&dev_info->measurements, (int) rssi);
			
			if ((dev_info = find_mac(rssi_list, alc_mac)) != NULL) {
				print_element(dev_info);
			}
			sem_post(&synchro);
		}
	}
	pcap_close(handle);
	pthread_exit((void *) 0);
}
