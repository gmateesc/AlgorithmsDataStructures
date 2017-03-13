This project implements a set of market data analysis functions. 

The market data that consists of two types of message. 
Each mtime-stamped message is either an insert (of an order 
at a given price), or an erase (of an earlier order). 
The market data is in ASCII text format such as the 
following:
```
  1300 I 100 10.0
  2100 I 101 13.0
  2300 I 102 13.0
  2500 E 101
  2600 E 102
  3100 E 100
```

The code provides the following functionality:

- maintains an _orderbook_, which is the set of outstanding
  orders that have been inserted but not erased;

- methods are provided to give the highest price of the 
  outstanding orders;

- an aplication that reads market data from a file and prints 
  out the time-weighted average of the highest price.


Additional information is in the [README.txt](https://github.com/gmateesc/AlgorithmsDataStructures/blob/master/MarketDataAnalysis/README.txt) file.
