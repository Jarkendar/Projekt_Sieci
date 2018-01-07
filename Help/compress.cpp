#include <iostream>
#include <zlib.h>
#include <zconf.h> 
#include <fstream>
#include <string.h>
#include <cmath>
using namespace std;
 
/*
 char* readName(char * data){
    char* header = new char[25];
    for (int i = 0; i < 20; i++)
    {
        header[i] = data[i];
        cout<<header[i]<<","<<data[i]<<";";
    }
    header[20] = '.';
    header[21] = 'z';
    header[22] = 'l';
    header[23] = 'i';
    header[24] = 'b';
    cout<<endl<<header<<endl;
    return header;   
 }
*/

//DO 100 MB pliki obslugujemy

int main()
{
    fstream plik, plik_po_zpakowaniu;
 
    plik.open("bigdem.txt",ios::binary|ios::in);
    if(!plik.is_open())  //blad otwarcia pliku
        return 0;        //koniec programu
 
    //sprawdzenie dlugosci pliku
    plik.seekg(0,ios::end);
    int dlugosc_pliku = plik.tellg();
    dlugosc_pliku += 100;
    plik.seekg(0,ios::beg);

    //naglowek
    char name[100]; 

    for (int i = 0; i < 100; i++)
    {
        name[i] = '\0';
    }
    
    //nazwa 
    for ( int i =0 ; i<85 ; i++ ) {
        name[i]='z';
    }
    
    //spacja
    name[85] = 32;
    
    //rozmiar pliku w naglowku
    for (int i = 86; i < 100; i++)
    {
        int number = dlugosc_pliku/(pow(10,100-i-1));
        if (number < 10)
        {
            name[i] = number+48;   
        }else{
            name[i] = number%10 + 48;
        }   
    }

    //cout<<"Rozmiar: "<<dlugosc_pliku<<endl<<"Name[99] :"<<name[99]<<endl;

    //tworzenie bufora do ktorego wczytamy plik
    char *bufor = new char[dlugosc_pliku];
    
    //zawartosc bufora
    cout<<"Buffor :";
    for (int i = 0; i < 100; i++){
        bufor[i] = name[i];
        cout<<bufor[i];
    }
    cout<<endl;

    //odczytujemy plik bajt po bajcie
    for(int i = 100;i<dlugosc_pliku;i++)
    {
        plik.read((char*)&bufor[i],1);
    }

    //nazwa pliku to zapisania
    char header[100];
    
    for (int i = 0; i < 100; i++)
    {
        header[i] = bufor[i];
    }


    for (int i = 0; i < 12; i++)
    {
        cout<<header[i];
    }
    

    string str = string(header, 100) + ".zlib";
    cout<<"nazwa pliku oczekiwana : "<<str<<endl;

    char * bufor_docelowy = new char[(int)(dlugosc_pliku*1.1 + 12)];
 
    unsigned long dlugosc_po_zpakowaniu; 
 
    //kompresujemy z najlepsza metoda
    cout<<"status kompresji "<<compress2((Bytef *)bufor_docelowy,&dlugosc_po_zpakowaniu,
    (const  Bytef*)bufor,dlugosc_pliku,Z_BEST_COMPRESSION);
    
    //tworzenie pliku w ktorym zapiszemy zpakowane dane
   
    plik_po_zpakowaniu.open(str.c_str(),ios::binary|ios::out);
 
    //zapis danych do pliku
    for(unsigned int i = 0; i<dlugosc_po_zpakowaniu;i++)
    {
        plik_po_zpakowaniu.write((char*)&bufor_docelowy[i],1);  
cout<<bufor_docelowy[i]<<",";
    }
 cout<<endl<<dlugosc_po_zpakowaniu<<endl;
    //zamykamy strumienie plikow
    plik.close();
    plik_po_zpakowaniu.close();
 
    return 0;
}
