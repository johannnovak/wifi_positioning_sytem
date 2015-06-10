
CC=../toolchain/bin/mips-openwrt-linux-gcc -g
LIB=-L../toolchain/lib/ -L../target/usr/lib/
INCLUDES=-I../toolchain/include/ -I../target/usr/include/ -I./

all: wifi_positioning 

wifi_positioning: main.o server.o helper.o pcap-thread.o rssi_list.o
	$(CC) -o wifi_positioning main.o server.o helper.o pcap-thread.o rssi_list.o $(LIB) $(INCLUDES) -lpcap -pthread -ldl -lm

socket: main.o server.o helper.o
	$(CC) -o socket main.o server.o helper.o -lpcap -pthread -ldl -lm $(LIB) $(INCLUDES) 

server.o:
	$(CC) -o server.o -c socket/server.c
	
helper.o:
	$(CC) -o helper.o -c socket/helper.c

main.o: 
	$(CC) -o main.o -c main.c $(LIB) $(INCLUDES)

pcap-thread.o:
	$(CC) -o pcap-thread.o -c rssi/pcap-thread.c $(LIB) $(INCLUDES)

rssi_list.o:
	$(CC) -o rssi_list.o -c rssi/rssi_list.c $(LIB) $(INCLUDES)

clean:
	rm *.o
