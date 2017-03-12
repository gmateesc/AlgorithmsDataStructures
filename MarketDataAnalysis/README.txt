
Table of contents
-----------------

  1. Directory sructure and files

  2. Code description

  3. Building and running the application

  4. Where is the required functionality provided

  5. The data type for the time stamp

---



1. Directory sructure and files
-------------------------------


My submission contains these files:


  src/               source code 

  Makefile           makefile to build the executable from source
 
  test_data.txt      sample market data input file, containing the 
                     input data given in the problem statement. 

  bin/  
      ob_app         application built by running make

      ob_app_test.sh sample script to run ob_app with test_data.txt


  README.txt         this file

---


2. Code description
-------------------

The console application code is in the file Application.cpp. 
Additinally:

1. the OrderBook class is defined in the files OrderBook.cpp 
   and OrderBook.h, and it manages Order objects that are 
   defined in the Order class, also defined in these files; 

2. the class AverageHighestPrice defined in Application.cpp is 
   used to compute the average highest price, as described in 
   Application.cpp 


The code uses C++11 features (such as std::isnan()) and STL.

Documentation of the code can be found in:

  o the comment blocks at the beginning of the 
    files Application.cpp and OrderBook.cpp;

  o inline comments in the files Application.cpp, 
    OrderBook.cpp and OrderBook.h.

In particular, the containers used to manage the orders in 
the OrderBook class and to implement the methods insert(), 
erase() and get_highest_price() are documented in OrderBook.cpp 
and OrderBook.h. 

---



3. Building and running the application
---------------------------------------

To build the application, run from the shell:

  make

which builds the executable bin/ob_app.

To run the application with the input data test_data.txt, 
run the script 

   bin/ob_app_test.sh

or simply run from the shell:

   make run

The file test_data.txt contains the input data given 
in the problem statement and running the program 
with this inut data prints the average maximum 
price of 10.5:

  $ make
  g++ -std=c++11 -stdlib=libc++ -c src/*.cpp
  g++ -std=c++11 -stdlib=libc++ -o bin/ob_app *.o
  rm -f *.o

  $ ./bin/ob_app_test.sh 
  Average highest price is 10.5

---



4. Where is the required functionality provided
-----------------------------------------------

The insert(), erase() and get_highest_price() 
methods of the OrderBook class are declared and 
defined in the files OrderBook.h and OrderBook.cpp. 

The average maximum price of the orders managed 
by the OrderBook is provided by the method get() 
in the class AverageHighestPrice (defined in 
the file Application.cpp), provided that after 
each operation order_book.insert(order) and 
order_book.erase(order) the programmer invokes:

  avg_max_price.update(order_book.get_highest_price(), order.get_timestamp());

on the object 

   AverageHighestPrice avg_max_price = AverageHighestPrice();

created before the first insert or erase operaion on the 
order book. 

This is documented in the file Application.cpp. 

---



5. The data type for the time stamp
-----------------------------------

The timestamp is defined in the problem statement 
as "milliseconds since start of trading". 

Because one day has 

  24 * 3600 * 100 = 8.64*10^7 msec 

an unsigned 32-bit integer 

  typedef uint32_t    Timestamp; 

is enough to represent 49 days of trading:

 (4 * 1024 * 1024 * 1024)/(24 * 3600 * 1000) = 49.71 
