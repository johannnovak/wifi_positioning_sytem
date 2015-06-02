#include <stdio.h>
#include <stdlib.h>
#include <pcap.h>
#include <pthread.h>

#include "rssi/pcap-thread.h"
#include "socket/helper.h"

volatile sig_atomic_t got_sigint;
Element* rssi_list = NULL;
sem_t synchro;

int main(int argc, char* argv[])
{	
	if(argc != 2)
	{
		printf("Usage: pcap-test [interface name]\n");
		return 0;
	}

	sem_init(&synchro, 0, 1);

	pthread_t tmain, tserv;
	pthread_create(&tmain, NULL, pcap_function, argv[1]);
	pthread_create(&tserv, NULL, server_start, argv[1]);
	
	int ret;
	pthread_join(tmain, &ret);
	pthread_join(tserv, &ret);
	printf("Thread terminated: %d\n", ret);

	return EXIT_SUCCESS;
}

