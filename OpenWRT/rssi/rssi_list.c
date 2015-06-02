#include "rssi_list.h"

u_char *string_to_mac(char * buf, u_char * byte_mac)
{
	int i;
	char hex[2];

	for(i=0 ; i<6 ; ++i)
	{
		// format: 00:00:00:00:00:00
		hex[0] = buf[i*3];
		hex[1] = buf[i*3+1];
		// buf[i*3+2] is the separator (or \0)
		
		byte_mac[i] = (u_char) strtol(hex, NULL, 16);
	}
	
	return byte_mac;
}

char * mac_to_string(u_char * byte_mac, char * buf)
{
	int i;
	char hex[2];
	
	for(i=0 ; i<6 ; ++i)
	{
		sprintf(hex, "%x", byte_mac[i]);
		buf[i*3] = hex[0];
		buf[i*3+1] = hex[1];
		
		if(i<5)
			buf[i*3+2] = ':';
		else
			buf[i*3+2] = '\0';
	}
	
	return buf;
}

void clear_outdated_values(Deque * list)
{
	Rssi_sample* h = list->head;
	Rssi_sample* prev = NULL;
	unsigned long long t = time(NULL);
	while(h != NULL)
	{
		if(h->deadline < t) // delete h
		{
			if(prev == NULL) // head
			{
				prev = h;
				h = h->next;
				free(prev);
				prev = NULL;
				list->head = h;
			}
			else // An element in the dequeue
			{
				Rssi_sample* tmp = h;
				h = h->next;
				free(tmp);
				tmp = NULL;
			}
			
			if(h == NULL) // the last element we deleted was the tail
				list->tail = prev;
			
		}
		else // keep h
		{
			prev = h;
			h = h->next;
		}
	}
}

void clear_values(Deque * list)
{
	Rssi_sample* tmp = NULL;
	Rssi_sample* h = list->head;
	while(h != NULL)
	{
		tmp = h;
		h = h->next;
		free(tmp);
		tmp = NULL;
	}
	
	list->head = NULL;
	list->tail = NULL;
}

void add_value(Deque * list, int value)
{
	unsigned long long t = time(NULL);
	Rssi_sample* newElem = (Rssi_sample*) malloc(sizeof(Rssi_sample));
	newElem->deadline = t + DEFAULT_KEEP_DELAY;
	newElem->rssi_mW = pow(10.0, (double)value / 10.0);
	newElem->next = NULL;
	
	if(list->tail == NULL) // empty list
	{
		list->tail = newElem;
		list->head = newElem;
	}	
	else
	{
		list->tail->next = newElem;
		list->tail = newElem;
	}
}

void clear_list(Element ** list)
{
	Element* elem = *list;
	Element* tmp = NULL;
	while(elem != NULL)
	{
		clear_values(&elem->measurements);
		tmp = elem;
		elem = elem->next;
		free(tmp);
		tmp = NULL;
	}
}

Element * find_mac(Element * list, u_char * mac_value)
{
	Element* elem = list;
	while(elem != NULL)
	{
		int i;
		int eq = 1;
		for(i=0 ; i<6 ; ++i)
		{
			if(elem->mac_addr[i] != mac_value[i])
			{
				eq = 0;
				break;
			}
		}
		
		if(eq == 1)
		{
			return elem;
		}
		
		elem = elem->next;
	}
	
	return NULL;
}

Element * add_element(Element ** list, u_char * mac_value)
{
	int i;
	Element* newElem = (Element*) malloc(sizeof(Element));
	for(i=0 ; i<6 ; ++i)
		newElem->mac_addr[i] = mac_value[i];
	newElem->measurements.head = NULL;
	newElem->measurements.tail = NULL;

	Element* elem = *list;
	if(elem == NULL)
	{
		newElem->next = NULL;
		*list = newElem;
		return newElem;
	}
	
	while(elem->next != NULL)
	{
		elem = elem->next;
	}
	newElem->next = NULL;
	elem->next = newElem;
	
	return *list;
}

void delete_element(Element ** list, Element * e)
{
	Element* elem = *list;
	Element* prev = NULL;
	
	while(elem != NULL)
	{
		if(elem == e) // delete elem
		{
			Element* tmp = NULL;
			if(prev == NULL) // elem is head
			{
				tmp = elem;
				elem = elem->next;
				clear_values(&tmp->measurements);
				free(tmp);
				tmp = NULL;
				*list = elem;
			}
			else
			{
				prev->next = elem->next;
				tmp = elem;
				elem = elem->next;
				clear_values(&tmp->measurements);
				free(tmp);
				tmp = NULL;
			}
		}
		else
		{
			prev = elem;
			elem = elem->next;
		}
	}
}

void clear_empty_macs(Element ** list)
{
	Element* elem = *list;
	while(elem != NULL)
	{
		if(elem->measurements.head == NULL)
		{
			Element* tmp = elem;
			elem = elem->next;
			delete_element(list, tmp);
		}
	}
}

void print_element(Element * element)
{
	printf("Mac Address: %x:%x:%x:%x:%x:%x.\n", element->mac_addr[0], element->mac_addr[1], element->mac_addr[2], element->mac_addr[3], element->mac_addr[4], element->mac_addr[5]);
	printf("\tMeasurments: \n");
	Rssi_sample* sample = element->measurements.head;
	int i=0;
	
	if(sample == NULL)
		printf("\tNo sample\n.");
	
	while(sample != NULL)
	{
		printf("\t#%d; %0.10f.\n", i, sample->rssi_mW);
		sample = sample->next;
		++i;
	}
	
	printf("EOF.\n\n");
}
