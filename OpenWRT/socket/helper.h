#ifndef HELPER_H
#define HELPER_H

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <semaphore.h>

#include <pthread.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

#include "../rssi/rssi_list.h"

void* server_start(void * _args);
void* client_start(void * _args);
void error(const char *msg);

#endif
