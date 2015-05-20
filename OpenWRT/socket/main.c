#include "helper.h"

int main(int argc, char* argv[])
{
	pthread_t server_thread, client_thread;
	
	pthread_create(&server_thread, NULL, server_start, NULL);
	pthread_join(server_thread, NULL);
	printf("Server thread finished.\n");

	return EXIT_SUCCESS;
}