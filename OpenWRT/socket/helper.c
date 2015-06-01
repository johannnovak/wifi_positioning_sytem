#include "helper.h"

void error(const char *msg)
{
	perror(msg);
	exit(0);
}
