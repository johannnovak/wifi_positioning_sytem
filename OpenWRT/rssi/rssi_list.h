#ifndef _RSSI_LIST_
#define _RSSI_LIST_

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>
#include <arpa/inet.h>
#include <sys/time.h>

#define DEFAULT_KEEP_DELAY 1000000

/*
 * Data definitions
 */

/*!
 * \struct Rssi_sample
 * \brief contains an RSSI sample value, extracted from a packet
 * It is a linked list.
 */
typedef struct _Rssi_sample
{
  double rssi_mW; ///< rssi_mW RSSI as mW value (=10^(rssi_dBm/10))
  unsigned long long deadline; ///< Time after which this sample shall be deleted
  struct _Rssi_sample * next; ///< next RSSI sample
} Rssi_sample;

/*!
 * \struct Deque
 * \brief defines a pseudo double ended queue
 * It shall contain the RSSI values sorted by deadline.
 */
typedef struct _Deque
{
  Rssi_sample * head; ///< Head (first element of deque)
  Rssi_sample * tail; ///< Tail (last element), useful for adding elements.
} Deque;

/*!
 * \struct Element
 * \brief contains one element of the Device list
 * The device list shall be sorted by device's MAC
 */
typedef struct _Element
{
  u_char mac_addr[6]; ///< MAC address in *binary* format
  Deque measurements; ///< deque with the actual measurements
  struct _Element *next; ///< next node (a different device)
} Element;

/*
 * Functions signatures
 */

// General functions
/*!
 * \brief Function string_to_mac converts a human-readable MAC to its binary counterpart.
 * \return the 6-bytes binary MAC
 * \param buf the buffer containing the MAC string
 * \param byte_mac a 6-byte buffer to store the result. byte_mac is returned by the function.
 */
u_char *string_to_mac(char * buf, u_char * byte_mac);

/*!
 * \brief mac_to_string opposite function to string_to_mac.
 * Takes a binary MAC address (such as extracted from IEEE802.11 header by libpcap)
 * and converts it to a human-readable string.
 * \return the string MAC
 * \param byte_mac the binary MAC
 * \param buf an already allocated char buffer, returned by the function.
 */
char * mac_to_string(u_char * byte_mac, char * buf);

// Rssi_sample functions
/*!
 * \brief clear_outdated_values removes all outdated RSSI in a RSSI deque.
 * \param list the deque from which to filter the outdated values.
 */
void clear_outdated_values(Deque * list);

/*!
 * \brief clear_values clears all the RSSI values from the deque
 * \param list the deque to clear
 */
void clear_values(Deque * list);

/*!
 * \brief add_value adds a new RSSI value at the end of the deque.
 * \param the deque to append the element to.
 * \param value the RSSI value (dBm, do not forget to convert when inserting!)
 */
void add_value(Deque * list, int value);

// Element functions
/*!
 * \brief clear_list fully clears the devices list (please free the RSSI values lists first!)
 * When the list is cleared, its value shall be NULL.
 * \param list pointer to the list head pointer.
 */
void clear_list(Element ** list);

/*!
 * \brief find_mac looks up for a MAC address in the list.
 * \return a pointer to the corresponding element, NULL if not found.
 * \param list the list head pointer.
 * \param mac_value the binary MAC to search.
 */
Element * find_mac(Element * list, u_char * mac_value);

/*!
 * \brief add_element adds an element (a new device node) to the list.
 * Elements shall be ordered.
 * \return the list head pointer (i.e. *list).
 * \param list a pointer to the list head pointer.
 * \param mac_value the binary MAC of the new node to be added.
 */
Element * add_element(Element ** list, u_char * mac_value);

/*!
 * \brief delete_element deletes an element from the list.
 * \param list a pointer to the list head pointer.
 * \param e a pointer to the element to be deleted.
 */
void delete_element(Element ** list, Element * e);

/*!
 * \brief clear_empty_macs deletes all the elements of the devices list whose RSSI deque is empty.
 * \param list a pointer to the list head pointer.
 */
void clear_empty_macs(Element ** list);

// Communications functions
/*!
 * \brief build_element builds the HTTP response part (part of the json sentence) for element e.
 * This function is used by build_buffer and build_buffer_full.
 * \return the json fragment string.
 * \param e the element whose data is required.
 * \param buf the string buffer to store the json fragment, returned by the function.
 */
char * build_element(Element * e, char * buf);

/*!
 * \brief build_buffer builds the full json sentence based on the positioning server request parameters.
 * \return the json message.
 * \param list the devices list.
 * \param buffer the string buffer to store the response, returned by this function.
 * \param my_name the human readable MAC of the access point.
 * \param macs_requested the list of MAC addresses requested by the server, binary format. Its length equals 6*nb_macs.
 * \param nb_macs the number of mac requested.
 */
char * build_buffer(Element * list, char * buffer, char * my_name,
		    u_char * macs_requested,
		    unsigned short nb_macs);

/*!
 * \brief build_buffer_full generated the json response for all the device list content.
 * \return the json response.
 * \param list the devices list.
 * \param my_name the human readable MAC of the access point.
 */
char * build_buffer_full(Element * list, char * buffer, char * my_name);

void print_element(Element * element);

#endif
