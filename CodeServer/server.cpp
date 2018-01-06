#include <iostream>
#include <zlib.h>
#include <zconf.h> 
#include <fstream>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <signal.h>
#include <dirent.h>
#include <errno.h>
#include<vector>
using namespace std;
int getdir (string dir, vector<string> &files)
{
    DIR *dp;
    struct dirent *dirp;
    if((dp  = opendir(dir.c_str())) == NULL) {
        cout << "Error(" << errno << ") opening " << dir << endl;
        return errno;
    }

    while ((dirp = readdir(dp)) != NULL) {
        files.push_back(string(dirp->d_name));
    }
    closedir(dp);
    return 0;
}
//todo adres ip serwera
int main(){    
    int socketDescriptor = socket(PF_INET, SOCK_STREAM, 0);
    	
    int on = 1;
    setsockopt(socketDescriptor,SOL_SOCKET, SO_REUSEADDR, (char*) &on, sizeof(on));
    
    struct sockaddr_in str;
    str.sin_family = PF_INET;
    str.sin_port = htons(12345);//numer portu
    str.sin_addr.s_addr = INADDR_ANY;
    
    bind(socketDescriptor,(struct sockaddr*) &str, sizeof(str));
    
    listen(socketDescriptor, 0);
    
    //signal(SIGCHLD, SIG_IGN);
    
    while(1){
        struct sockaddr_in newClient;
        socklen_t size;    
        
        int client = accept(socketDescriptor, (struct sockaddr*)&newClient, &size);
        
        if (fork() == 0){    
            close(socketDescriptor);	
            if(client != -1){
            	//TODO cały kompunikat = "literka komunikatu(1)||header(100)||reszta pliku(n)"
                int maxRead = 1;
                char messageBufforHeader[1];
                read(client, &messageBufforHeader, 1);
                char typeMessage = messageBufforHeader[0];
                switch(typeMessage){
                    case 'c':{//kompresja
//WCZYTANIE DANYCH OD KLIENTA                    	
                        cout<<client<< " Compression: ";
                        //char bufferToFile[1000];
                        
                        char header[100];// = new char(100);
                        for (int i = 0; i < 100; i++)
                        {
                        	char buf[1];
                        	read(client, &buf, 1);
                        	//bufferToFile[i] = buf[0];
                        	header[i] = buf[0];
                        }
                        int fileSize = 0;
                        int start = 0;
                        for (int i = 86; i <100; i++)//wczytanie rozmiaru pliku wejściowego
                        {
                        	int number = header[i] - 48;
                                cout<<"header[i] " << header[i]<<endl;
                                cout<<"rozmiar " << number<<endl;
                                
                        	if (number != 0)
                        	{
                        		start = 1;
                        	}
                        	if (start == 1)
                        	{
                        		fileSize = fileSize*10 + number;	
                        	}
                        }
                        cout<<"file size : "<<fileSize<<endl;
                        
                        cout<<"Doczytuje dane " <<endl;
                        char * bufferToFile = new char[fileSize];
                        for (int i=0 ; i<100 ; i++) {
                            bufferToFile[i]=header[i];
                            
                        }
                        //delete(header);
                      int actualByte = 100;
                        char tmpBufforForData[maxRead];
                        do{                                 // wczytywanie pliku ze strumienia
                            
                           int readed = read(client, &tmpBufforForData, maxRead);
                           for (int i = 0; i < readed; i++)//przepisanie wczytanych plików
                           {
                           		bufferToFile[actualByte] = tmpBufforForData[i];
                           		actualByte++;
                                     //   cout<<"actualByte" << actualByte<<endl;
                           }
                        }while(actualByte != fileSize);
                        cout<<"Wczytałem cały strumień wejściowy."<<endl;
//KOMPRESJA
                        cout<<"Rozpoczynam kompresję"<<endl;
                     
                        
                        char* compressFileBuffer = new char[(int)(fileSize*1.1 + 12)];
                                        
    					unsigned long lenghtAfterCompress; 
                                       
                                        
					    //kompresujemy z najlepsza metoda
					    compress2((Bytef *)compressFileBuffer,&lenghtAfterCompress,
					    (const  Bytef*)bufferToFile,fileSize,Z_BEST_COMPRESSION);
                                          cout<<"rxxx"<<endl;
					    string fileName = string(header,100)+".zlib";
                                                
    					//tworzenie pliku w ktorym zapiszemy zpakowane dane
    					fstream fileToCompress;
    					fileToCompress.open(fileName.c_str(),ios::binary|ios::out);
					 
					    for(unsigned int i = 0; i<lenghtAfterCompress;i++)
					    {
					        fileToCompress.write((char*)&compressFileBuffer[i],1);  
					    }
 						fileToCompress.close();
 						cout<<client<<" : "<<"skończyłem kompresję"<<endl;
                                                
                        delete(compressFileBuffer);
                        delete(bufferToFile);
                        break;
//KONIEC KOMPRESJI
                    }
                    case 'd':{//dekompresja
                            //zalozylem se klient bedzie przesylal nazwa_archiwum.zlib ktory chce zeby mu zdekompresowac (105 znakow )
                             cout<<" Id :  " << client<< " Dekompresja... "<<endl;
                        char nameArchive[105];//
                        for (int i = 0; i < 105; i++)
                        {
                        	char buf[1];
                        	read(client, &buf, 1);
                        	
                        	nameArchive[i] = buf[0];
                                //cout<<nameArchive[i];
                        }
                    
                        string testArchiveName= string(nameArchive);
                        string archiveName = testArchiveName.substr(0,105);
                        cout<<endl<<" Nazwa archiwum : "<<endl <<archiveName<<endl;
                       // cout<<"Lista plikow do wyslania " <<endl<<nameArchive<<endl;
                        break;
                    }
                    case 'w':{//wyświetl listę arch
                        string dir = string(".");
						    vector<string> files = vector<string>();

						    getdir(dir,files);
                                                   int sizeNameFiles=0;
						    for (unsigned int i = 0;i < files.size();i++) {
						      //  cout << files[i] << endl;
                                                        string fileName=files[i];
                                                        sizeNameFiles+=fileName.size()+1;
                                                        
						    }
						    
						   // cout<<"rozmiar tablicy"<<sizeNameFiles<<endl;
                                                
						    char tableToSend [sizeNameFiles];
                                                    int iterator =0;
                                                    for ( unsigned int i=0; i<files.size() ; i++ ) {
                                                        
                                                        string fileName=files[i];
                                                        
                                                        for (unsigned int j=0; j<fileName.size();j++){
                                                            
                                                            tableToSend[iterator]=fileName[j];
                                                            iterator++;
                                                        }
                                                        
                                                        tableToSend[iterator]='\n';
                                                        iterator++;
                                                        
                                                        
                                                    }
                                                    
                                                    cout<<"Lista plikow do wyslania " <<endl<<tableToSend<<endl;
                                                    
                                                    write(client,&tableToSend,sizeof(char)*sizeNameFiles);
						    
						    

                        break;
                    }


                }
                
                // do{//czytanie wiadomości
                //     read(client, &bufforToRead, maxRead);
                //     printf("%c", bufforToRead[0]);
                //     message[j] = bufforToRead[0];
                //     j += i;
                // }while ( bufforToRead[0] != '\n');
                // printf("%s\n", message);
                // if(strcmp(message,"127228\n") == 0){
                    
                //     write(client, buff, sizeof(buff));
                // }else if(strcmp(message,"127265\n")== 0){
                //     char buff[24];
                //     sprintf(buff, "JAROSLAW SKRZYPCZAK %d\n", i);
                //     printf("%s\n", buff);
                //     write(client, buff, sizeof(buff));
                // }else{
                // char buff[20];
                // sprintf(buff, "I DONT KNOW YOU %d\n", i);
                // printf("%s\n", buff);
                // write(client, buff, sizeof(buff));
                // }
            }
            printf("Client adress ( connect ): %s\n", inet_ntoa((struct in_addr) newClient.sin_addr));
            exit(0);
        }
        close(client);
    }




	return 0;
}
