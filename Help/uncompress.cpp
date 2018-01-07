#include <iostream>
#include <zlib.h>
#include <zconf.h> 
#include <fstream>
using namespace std;
 
int main()
{
    fstream plik, plik_po_rozpakowaniu;
 
    plik.open("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz 00000000000254.zlib",ios::binary|ios::in);
    if(!plik.is_open())  //blad otwarcia pliku
        return 0;   //koniec programu
 
    //sprawdzenie dlugosci pliku
    plik.seekg(0,ios::end);
    unsigned int dlugosc_pliku = plik.tellg();
    plik.seekg(0,ios::beg);
 
    //tworzenie bufora do ktorego wczytamy plik
    char *bufor = new char[dlugosc_pliku];
 
    //odczytujemy plik bajt po bajcie
    for(unsigned int i = 0;i<dlugosc_pliku;i++)
    {
        plik.read((char*)&bufor[i],1);  
    }
 
    //tworzenie bufora do ktorego zapiszemy dane rozpakowane przez biblioteke zlib
    //wielkosc bufora dla bezpieczenistwa powinna byc 
    //odpowiednio duza aby rozpakowane dany zmiescily sie
    //mozna bylo oczywiscie zapisac rozmiar danych przed spakowaniem do pliku 
    //maksymalnie dane mogą mieć wielkość 5000 bajtow
    //jezeli mamy wiekszy plik to odpowiedni musimy zwiekszyc ta wartosc
    char * bufor_docelowy = new char[800000];//trzeba przerobić aby miał gdzieś zapisany rozmiar 
 
    unsigned long dlugosc_po_rozpakowaniu; 
 
    //rozpakowyjemy
   cout<<"Status uncompressed " <<  uncompress((Bytef*)bufor_docelowy,(uLong*)&dlugosc_po_rozpakowaniu,(Bytef*)bufor,dlugosc_pliku);
     
    //tworzenie pliku w ktorym zapiszemy rozpakowane dane
    plik_po_rozpakowaniu.open("rozpakowany.txt",ios::binary|ios::out);//gdzieś trzeba trzymać zapisane dane rozszerzenia
    //zapis danych do pliku
    for(unsigned int i = 0; i<dlugosc_po_rozpakowaniu;i++)
    {
        plik_po_rozpakowaniu.write((char*)&bufor_docelowy[i],1);    
    }
    //zamykamy strumienie plikow
    plik.close();
    plik_po_rozpakowaniu.close();
    return 0;
}
