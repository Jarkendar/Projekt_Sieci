#include <iostream> 
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
#include <vector>
#include <zip.h>

using namespace std;
int getdir (string dir, vector<string> &files)//pobranie zawartości folderu serwera
{
    DIR *dp;
    struct dirent *dirp;
    if((dp  = opendir(dir.c_str())) == NULL) {
        cout << "Error(" << errno << ") opening " << dir << endl;
        return errno;
    }
    while ((dirp = readdir(dp)) != NULL ) {
        
        files.push_back(string(dirp->d_name));
    }
    closedir(dp);
    return 0;
}

int main(){    
    int socketDescriptor = socket(PF_INET, SOCK_STREAM, 0);
    int HEADERSIZE = 110;//stały rozmiar nagłówka
    int on = 1;
    setsockopt(socketDescriptor,SOL_SOCKET, SO_REUSEADDR, (char*) &on, sizeof(on));
    
    struct sockaddr_in str;
    str.sin_family = PF_INET;
    str.sin_port = htons(12345);//numer portu
    str.sin_addr.s_addr = INADDR_ANY;
    
    bind(socketDescriptor,(struct sockaddr*) &str, sizeof(str));
    
    listen(socketDescriptor, 0);

    while(1){
        struct sockaddr_in newClient;
        socklen_t size;    
        
        int client = accept(socketDescriptor, (struct sockaddr*)&newClient, &size);
        //TWORZENIE NOWEGO WĄTKU DLA KLIENTA
        if (fork() == 0){    
            close(socketDescriptor);	
            if(client != -1){
            	//TODO cały kompunikat = "literka komunikatu(1)||header(100)||reszta pliku(n)"
                int maxRead = 1;
                //ODCZYTANIE ZNAKU POLECENIA
                char messageBufforHeader[1];
                read(client, &messageBufforHeader, 1);
                char typeMessage = messageBufforHeader[0];
                switch(typeMessage){
                    case 'c':{//c => KOMPRESJA
                        //WCZYTANIE DANYCH OD KLIENTA                    	
                        cout<<client<< " Compression: ";
                        char header[HEADERSIZE];
                        for (int i = 0; i < HEADERSIZE; i++){
                            char buf[1];
                            read(client, &buf, 1);
                            header[i] = buf[0];
                        }
                        int fileSize = 0;
                        int start = 0;
                        for (int i = 86; i <100; i++){//wczytanie rozmiaru pliku wejściowego
                            int number = header[i] - 48;
                            if (number != 0){
                                start = 1;
                            }
                            if (start == 1){
                                fileSize = fileSize*10 + number;	
                            }
                        }
                        
                        cout<<"Rozmiar przeslanych danych od klienta  : "<<fileSize<<endl;
                        
                        char * bufferToFile = new char[fileSize];
                        
                        for (int i=0 ; i<HEADERSIZE ; i++) {
                            bufferToFile[i]=header[i];
                        }
                        
                        int actualByte = HEADERSIZE;
                        char tmpBufforForData[maxRead];
                        do{//doczytanie pliku ze strumienia
                           int readed = read(client, &tmpBufforForData, maxRead);
                           for (int i = 0; i < readed; i++){//przepisanie wczytanych plików
                                bufferToFile[actualByte] = tmpBufforForData[i];
                                actualByte++;   
                           }
                        }while(actualByte != fileSize);
                        
                        cout<<endl<<"Wczytałem cały strumień wejściowy."<<endl;
                        
                        //KOMPRESJA             
                        //tworzymy folder dla archiwow .zip przelacznik -p bo jakby juz byl folder
                        system("mkdir -p archives");
                        //folder do trzymania strumieni klientow do zipowania
                        system("mkdir -p tmpFilesForZip");
                        
                        cout<<"Rozpoczynam kompresję"<<endl;
                        
                        string adressClient= string(header); //inet_ntoa((struct in_addr) newClient.sin_addr);
                        
                        //   string fileName = "./"+adressClient+"/"+string(header,110);
                        string fileName = "./tmpFilesForZip/"+adressClient;//+string(header,110);
                        cout<<"Miejsce zapisania strumienia : "<<fileName<<endl;
                        
                        //zapis strumienia do pliku tymczasowego
                        fstream fs;
                        fs.open(fileName.c_str(), ios::binary|ios::out);
                        for(int i = 0; i<fileSize;i++){
                            fs.write((char*)&bufferToFile[i],1); 
                        }
                        fs.close();
                        
                        //zip ./archives/first.zip ./tmpFilesForZip/127.0.0.1 -j  -FS -r
                        //-FS na potrzeby loopbacka
                        
                        string zipCommand ="zip ./archives/" + adressClient + ".zip" + "  ./tmpFilesForZip/"+adressClient+" -j  -FS -r ";
                        
                        system (zipCommand.c_str());
                        
                        cout<<"Uworzylem zipa dla klienta o IP " << adressClient <<endl;
                        
                        string rmCommand = "rm " + fileName;
                        system (rmCommand.c_str());
                        break;
                    //KONIEC KOMPRESJI
                    }
                    case 'd':{//d => DEKOMPRESJA
                        cout<<" Id :  " << client<< " Dekompresja... "<<endl;
                        //WCZYTYWANIE NAGŁÓWKA
                        char nameArchive[HEADERSIZE+4];
                        for (int i = 0; i < HEADERSIZE+4; i++){
                        	char buf[1];
                        	read(client, &buf, 1);
                        	nameArchive[i] = buf[0];
                        }
                    
                        string testArchiveName= string(nameArchive);
                        string archiveName = "./archives/"+testArchiveName.substr(0,HEADERSIZE+4);
                        cout<<endl<<"Nazwa archiwum : "<<endl <<archiveName<<endl;
                      
                        //sprawdzenie czy odczytana nazwa archiwum jest w katalogu
                        string dir = string("./archives");
                        vector<string> files = vector<string>();

                        getdir(dir,files);
                    
                        for (unsigned int x = 0;x < files.size();x++) {
                            if (archiveName.compare(files[x])==0){
                                cout<<"TEN PLIK ISTNIEJE W KATALOGU"<<endl;
                            }
                        }
                        
                        string unzipCommand= "unzip -u " + archiveName + " -d tmpFilesUnzipped";
                        system(unzipCommand.c_str());
                        
                        fstream archive;
                        
                        // 9 pierwszych po IP 127.0.0.1 potem trzeba dodac dynamicznie po czytaniu znakow zmienna na gorze
                        string archiveNameOpen="./tmpFilesUnzipped/"+testArchiveName.substr(0,HEADERSIZE);
                        
                        cout<<"nazwa pliku open: " <<archiveNameOpen <<endl;
                        
                        //otwieranie pliku
                        archive.open(archiveNameOpen.c_str(),ios::binary|ios::in);
                        if(!archive.is_open()) return 0;
                            
                        //sprawdzenie dlugosci pliku
                        archive.seekg(0,ios::end);
                        unsigned int archiveLength = archive.tellg();
                        archive.seekg(0,ios::beg);
                        
                        cout<<"Dlugosc pliku : " <<archiveLength <<endl;
                        
                        //tworzenie bufora do klienta
                         char buffer[archiveLength];
                         
                            for(unsigned int i = 0;i<archiveLength;i++)
                            {
                                archive.read((char*)&buffer[i],1); 
                            }
                        
                            cout<<"Odczytalem buffor " <<endl;
                            
                        //wyslanie rozpakowanego zipa do clienta
                      
                            write(client, &buffer, archiveLength);
                            
                      //   unsigned int numberOfSendingChars =0;
                      //  char tmpBufforForData;
                     //   
                     //   do{   
                     //       tmpBufforForData=buffer[numberOfSendingChars];
                      //      int readed = write(client, &tmpBufforForData, 1);
                     //       numberOfSendingChars+=readed;
                      // 
                     //       }while(numberOfSendingChars!= archiveLength);
                      
                        string rmCommand = "rm -f "+ archiveNameOpen;
                        system(rmCommand.c_str());
                        break;
                    //KONIEC DEKOMPRESJI
                    }
                    case 'w':{//w => POBIERZ LISTĘ DOSTĘPNYCH ARCHIWÓW
                        string dir = string("./archives");
			vector<string> files = vector<string>();

                        getdir(dir,files);
                        int sizeNameFiles=0;
			for (unsigned int i = 0;i < files.size();i++) {
                            string fileName=files[i]; 
                            if (fileName!="." && fileName!=".."){
                            sizeNameFiles+=fileName.size()+1; 
                            }
                        }		
                                                
                        char tableToSend [sizeNameFiles];
                        int iterator =0;
                        for ( unsigned int i=0; i<files.size() ; i++ ) {
                            string fileName=files[i];
                            if (fileName!="." && fileName!="..") {
                                for (unsigned int j=0; j<fileName.size();j++){
                                    tableToSend[iterator]=fileName[j];
                                    iterator++;
                                }
                                tableToSend[iterator]='\n';
                                iterator++;
                            }  
                        }
                        
                        cout<<"Lista dostepnych archiwow do wyslania " <<endl<<tableToSend<<endl;
                        
                        int numberOfSendingChars =0;                        
                            
                        char tmpBufforForData;
                        do{// wysyłanie strumienia 
                            tmpBufforForData=tableToSend[numberOfSendingChars];
                            int readed = write(client, &tmpBufforForData, 1);
                            numberOfSendingChars+=readed;
                        }while(numberOfSendingChars!= sizeNameFiles);

                        break;
                    //KONIEC POBIERANIA LISTY
                    }
                }    
            }
            printf("Client adress closed : %s\n", inet_ntoa((struct in_addr) newClient.sin_addr));
            cout<<endl;
            exit(0);
        }
        close(client);
    }
    return 0;
}
