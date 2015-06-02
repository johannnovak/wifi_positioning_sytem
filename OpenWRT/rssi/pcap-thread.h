#ifndef _PCAP_THREAD_
#define _PCAP_THREAD_

#include <sys/types.h>
#include <pcap.h>
#include <semaphore.h>
#include <signal.h>

#include "rssi_list.h"

struct ieee80211_header
{
  u_short frame_control;
  u_short frame_duration;
  u_char recipient[6];
  u_char source_addr[6];
  u_char address3[6];
  u_short sequence_control;
  u_char address4[6];
};

struct ieee80211_radiotap_header
{
  u_char it_version;
  u_char it_pad;
  u_char it_len[2];
  u_char it_present[4];
};

void *pcap_function(void *arg);

#endif /* _PCAP_THREAD_ */
