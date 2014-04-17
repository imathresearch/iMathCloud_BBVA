This contains the source code of the API - DLL for generic connection to iMathCloud through Win-32 applications,
using libcurl. Header files from the directoy include are extracted from libcurl,as well as the dll, which
are pre-compiled for Win-32 architectures. 

We refer to the iMath wiki for further explanation:

http://wiki.imathresearch.com/doku.php?id=services:butler_scientifics:tasks#butler_scientifics_integration

To compile and link the test.cpp file, open a DOS console, go to the directory src, and write: 

g++ -I../include -L. test.cpp -lcurl -o test.exe 

To properly compile the dll (it generates the dynamic library imathcloud.dll, and the static library libimathcloud.a)

g++ -c -DBUILDING_EXAMPLE_DLL -I../include imathcloud.cpp
g++ -shared -static-libstdc++ -static-libgcc -L. -o imathcloud.dll imathcloud.o json*.o -lcurl -Wl,--out-implib,libimathcloud.a
(The above line requires the already compliled versions of JSONCPP, see below)

To compile and link example.cpp, which uses the imathcloud.dll:

g++ -static-libstdc++ -static-libgcc -L. example.cpp -limathcloud -o example.exe


* JSON treatement:
We use jsoncpp, recommended by www.json.org for C++ developments. It looks pretty good