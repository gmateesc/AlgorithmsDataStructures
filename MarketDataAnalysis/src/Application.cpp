/**
 * \brief Console application 
 *
 *  This application uses the OrderBook class (see 
 *  OrderBook.{h,cpp} to analyse a set of market data 
 *  that contains two types of messages: insert and erase.
 *
 *  The application reads input data from a file and prints 
 *  out the time-weighted average of the highest price.
 *
 *  The input data is in a text file (see sample test_data.txt) that 
 *  contains one message per line: each time-stamped message is either 
 *  an insert of an order at a price, or an erase an earlier order. 
 */

#include "OrderBook.h"

/** 
 * Helper class used to compute highest average price.
 * To get the highest average price, invoke after each 
 * order_book.insert(order) and order_book.erase(order): 
 *
 *   update(order_book.get_highest_price(), order.get_timestamp());
 */
class AverageHighestPrice {

private:

  // State used to compute highest average price 
  double              Sum;     // sum of highest prices, weighed by their duration 
  trading::Timestamp  Time;    // elapsed time for the value of Sum
  trading::Timestamp  t_start; // time when the value of max became valid
  double              max;     // highest price, valid starting with time t_start

public:

  AverageHighestPrice () {
    max     = trading::NaN;  // value before processing any order 
    Time    = 0;       
    Sum     = 0.0; 
    t_start = 0; 
  }

  /** \brief 
   * Returns the highest average price 
   */
  double get ( ) {

    double res = trading::NaN;
    if ( Time > 0 ) {
      res = (Sum / Time);
    }
    return res;
  }

  /**
   * \brief
   * Update the local state according the following algorithm, 
   * where operations must be executed in this order, i.e., 
   * update max after setting Time and Sum and t_start last
   *
   *   Time  += (ts - t_start)       if max != NaN
   *   Sum   += max * (ts - t_start) if max != NaN
   *   max    = new_max
   *   t_start = ts  
   *
   * where 'ts' is the timestamp of the latest insert or erase 
   * operation, and 'new_max' is the highest price in the order-book 
   * after that latest operation
   */
  void update (double new_max, trading::Timestamp ts ) {

    if ( ! std::isnan(max) ) {
      Time += (ts - t_start);
      Sum  += max * (ts - t_start);
    }

    max    = new_max;
    t_start = ts;
  } 

}; // class AverageHighestPrice 


/**
 * \brief Main program
 */
int main(int argc, char* argv[]) {
  
  //
  // 1. Check command line arguments; correct usage is "program DATA_FILE"
  //
  if ( argc != 2 ) {
    cerr << "# ERROR: " << "Must run with: program INPUT_FILE_NAME" << endl; 
    abort();
  }
  string filename = argv[1];   


  //
  // 2. The OrderBook that maintains outstadning oders and the object 
  //    used to compute the average 
  //
  trading::OrderBook order_book = trading::OrderBook(); // create empty order book
  AverageHighestPrice avg_max_price = AverageHighestPrice();

  
  //
  // 3. Load messages to process from the input file into the 'data' object
  //
  trading::MarketData data = trading::MarketData(filename);  
  try {
    data.getMarketData();
  } 
  catch (const trading::OrderBookError & ex) {
    cerr << "# ERROR: " << "Cannot read input file" << endl << ex.what() << endl;
    abort();
  }


  //
  // 4. Process each message in the 'data' object, building a temp Order 
  //    object from it and then doing an insert() or erase() in the OrderBook.
  //
  while ( data.hasNextMessage() ) {    

    //
    // 4.1 Extract next nessage to process
    //
    string msg = data.nextMessage();
    //cout << msg << endl;     
    
    //
    // 4.2 Tokenize message and build a temp order object from the tokens
    //
    vector<string> msg_tokens = trading::MarketData::tokenize(msg, ' ');
    char           msg_type; 
    trading::Order order;  // temporary object built from msg_tokens

    try {
      msg_type = trading::MarketData::get_msg_type(msg_tokens);  
      order    = trading::Order(msg_tokens);
    } 
    catch (const trading::OrderBookError & ex) {
      cerr << "# ERROR: " << "Cannot parse message \"" << msg << "\"" << endl;  
      continue;
    }    
    
    //
    // 4.3 Use the temp Order object to insert or erase from the order book
    //
    switch (msg_type) {
	
    case 'I':  // Insert order in order book
      order_book.insert(order);
      break;
    case 'E':  // Erase order from order book
      order_book.erase(order);
      break;
    default:   // Illegal mesage type
      throw trading::OrderBookError("Order type must be 'I' or 'E'");
    }      
        
    //
    // 4.4 Print the order book
    //
    //cerr << "# DEBUG: " << "Order book contains: " << endl << order_book.print() << endl; 


    //
    // 4.5 Update state of the object used to compute average highest price
    // 
    avg_max_price.update(order_book.get_highest_price(), order.get_timestamp());

  } // while () { ... }


  //
  // 5. Print the average highest price
  //
  cout << "Average highest price is " << avg_max_price.get() << endl; 
  return 0;
}
