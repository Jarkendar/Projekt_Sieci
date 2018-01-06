#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <unistd.h>

#define ANSI_COLOR_RED      "\x1b[31m"
#define ANSI_COLOR_RESET    "\x1b[0m"
#define ANSI_COLOR_GREEN    "\x1b[32m"

const char IP_ADDR[14] = "127.0.0.1";
const int IP_PORT = 12345;
char buf[200];

int main(int argc, char *argv[]) {
        struct sockaddr_in addr;
        addr.sin_family = PF_INET;
        addr.sin_port = htons(IP_PORT);
        addr.sin_addr.s_addr = inet_addr(IP_ADDR);
        
        // Connect with server
        int sd = socket(PF_INET, SOCK_STREAM, 0);
        int connect_status = connect(sd, (struct sockaddr*)&addr, sizeof(addr));
        
        if(connect_status != -1) {
            // Send Student ID
            char wielka_tablica[1001];

            for (int i = 0; i < 1001; i++)
            {
                wielka_tablica[i] = 'c';
            }

            for (int i = 87; i < 101; i++)
            {
                wielka_tablica[i] = '0';
            }
            wielka_tablica[97] = '1';

            write(sd, &wielka_tablica, sizeof(char)*1001);
            
            // Get server response & write to console
            
            
            
            // Close connect
            close(sd);
            return 0;
            
        } 
}
