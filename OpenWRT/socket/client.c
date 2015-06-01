#include "helper.h"

void* client_start(void * _args)
{
	int sockfd, portno, n;
	struct sockaddr_in serv_addr;
	struct hostent *server;

	char buffer[256];

	// Initializing the socket
	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if(sockfd < 0)
		error("ERROR: opening socket");

	// Initializing serv_addr to zero
	memset( (char*) &serv_addr, 0, sizeof(serv_addr) );
	
	// listening on port #3000
	portno = 3000;

	// Getting server infos
	server = gethostbyname("SHARUNDAAR-PC");
	if(server == NULL)
		error("ERROR: no such host");

	// Setting serv_addr values
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_port = htons(portno); // convert portno to network byte order
	memcpy((char*) &serv_addr.sin_addr.s_addr, (char*) server->h_addr, server->h_length);
	if(connect(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0)
		error("ERROR: connecting");

	printf("Please enter the message: ");
	memset(buffer, 0, 256);
	fgets(buffer, 255, stdin);
	n = write(sockfd, buffer, strlen(buffer));
	if(n<0)
		error("ERROR: writing to socket");

	close(sockfd);
	
	return EXIT_SUCCESS;
}