#include "helper.h"

void* connection_handler(void * _args)
{
	char buffer[256];
	int clientSocket = * ((int*) _args);
	printf("<CT> Handling connection %d.\n", clientSocket);

	memset(buffer, 0, 256*sizeof(char));
	int n = read(clientSocket, buffer, 255);
	if(n < 0)
		error("ERROR: reading from socket");
	printf("<CT> Here is the message: %s\n", buffer);

	printf("<CT> Now sending back a message.\n");
	n = write(clientSocket, "I got your message.\n", 20);
	if(n < 0)
		error("ERROR: writing to socket");

	shutdown(clientSocket, 2);
	close(clientSocket);

	if(strcmp(buffer, "exit") == 0)
		pthread_exit(0);
	pthread_exit(1);
}

void* server_start(void * _args)
{
	char buffer[256];
	int socketfd, portno;
	int clSocketFd;
	struct sockaddr_in serv_addr, cli_addr;
	socklen_t clilen;

	// Initializing the socket
	socketfd = socket(AF_INET, SOCK_STREAM, 0);
	if(socketfd < 0)
		error("ERROR: opening socket");

	// Initializing serv_addr to zero
	memset( (char*) &serv_addr, 0, sizeof(serv_addr) );

	// listening on port #3000
	portno = 3000;

	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = INADDR_ANY;
	serv_addr.sin_port = htons(portno);
	if(bind(socketfd, (struct sockaddr *) &serv_addr,
		sizeof(serv_addr)) < 0)
		error("ERROR: on binding");

	listen(socketfd, 5);

	clilen = sizeof(cli_addr);

	int loop = 1;
	while(loop)
	{
		clSocketFd = accept(socketfd,
							(struct sockaddr *) &cli_addr,
							&clilen);
		if(clSocketFd < 0)
			error("ERROR: on accept");
		printf("New connection established\n");

		// Handling the connection in a thread
		pthread_t clHandler;
		pthread_create(&clHandler, NULL, connection_handler, &clSocketFd);
		int* result = (int*) malloc(sizeof(int));
		pthread_join(clHandler, result);
		printf("Connexion ended with value %d.\n", *result);
		loop = *result;
		free(result);
	}

	printf("Closing Socket.\n");
	shutdown(socketfd, 2);
	close(socketfd);

	return 0;
}