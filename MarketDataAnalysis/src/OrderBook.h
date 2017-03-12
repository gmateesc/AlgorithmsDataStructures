/**
 * Header file to be included by OrderBook.cpp and 
 * by applications that use the OrderBook class
 */

#ifndef ORDERBOOK_H_
#define ORDERBOOK_H_

#include <vector>
#include <map>
#include <unordered_map>
#include <string>
#include <iostream>
#include <fstream>
#include <sstream>
#include <algorithm>
#include <stdexcept>   
#include <limits>
#include <cstdint>

using namespace std;

namespace trading {

  //
  // 0. Type and const definitions
  //

  typedef double      OrderPrice;     // limit price 
  typedef uint32_t    OrderId;        // unique order ID
  typedef uint32_t    Timestamp;      // timestamp (msec) 1day = 8.64*10^7 msec 

  static const double NaN = std::numeric_limits<double>::quiet_NaN(); // NaN value

  class Order;  // Forward declaration 


  //
  // 1. OrderBook class
  //
  class OrderBook {

  public:
    
    // Default constructor
    OrderBook();

    // Insert method
    void insert ( const trading::Order& order);

    // Erase method
    void erase ( const trading::Order& order);

    // Get the order with the highest price
    OrderPrice get_highest_price(void); 
    
    // Print the order book orders
    string print();


  private:

    // Multimap (order.price -> order) to keep orders sorted by decreasing price
    typedef multimap<trading::OrderPrice, trading::Order, greater<trading::OrderPrice> > OrderMultimap;

    // Hashmap (order.id -> OrderMultimap::iterator) to lookup orders by orderID
    typedef unordered_map<trading::OrderId, OrderMultimap::iterator>  OrderHash;
    
    OrderMultimap ob_Map_; 
    OrderHash     ob_Hash_; 
            
  }; // class OrderBook
  


  //
  // 2. Order class
  //
  class Order {

    friend class OrderBook;

  public:
    Order();                 // default constructor
    Order(vector<string> &); // single-arg constructor

    Timestamp  get_timestamp(void); 
    string     toString() const;

  private:
    OrderId         id;         // unique ID
    Timestamp       timestamp;  // milliseconds 
    OrderPrice      price;      // price 

  }; // class Order



  //
  // 3. MarketData class 
  //

  class MarketData {

  public:

    // Default constructor
    MarketData( ); 

    // Single-argument constructor
    explicit MarketData(string filename ) {
      messages_ = vector<string>();
      cur_      = messages_.begin();
      filename_ = filename;
    }

    // Read orders from input data file into messages_
    void getMarketData();

    // Get next message from the market data file
    const string & nextMessage();
   
    // Test if there is a next message in the input
    bool hasNextMessage();

    // Tokenize message 
    static vector<string> tokenize(const string& str, const char& delim);

    // Get message type from tokenized message
    static char get_msg_type (vector<string> & msg_tokens);


  private:
    string         filename_;      // Input file whose contents to load into messages_
    vector<string> messages_;      // The data read from the input file 
    vector<string> setMessages();  // Method that loads contents of filename_ into messages_
    vector<string>::iterator cur_; // Iterator to messages_

  }; // class MarketData
  


  // 
  // 4. Exceptions
  //
  
  class OrderBookError: public std::runtime_error {

  public:
    OrderBookError()                    : std::runtime_error("OrderBookError") { }
    OrderBookError(const string & str ) : std::runtime_error("OrderBookError " + str) { }

  }; // class OrderBookEror
  

} // namespace trading { ... }



//
// 5. Definitions of inline methods
//

//
// 5.1 OrderBook methods
//

/** 
 * \brief
 *   Returns highest price of an order in the order book, 
 *   if the order book is not empty, or NaN otherwise.
 */
inline trading::OrderPrice trading::OrderBook::get_highest_price() {

  double res = trading::NaN;

  OrderMultimap::iterator it = ob_Map_.begin();
  if ( it != ob_Map_.end() ) {
    res = it->first;
  }
  return res;
}


//
// 5.2 Order methods
//

/**
 * \brief Print an Order object 
 */
inline string trading::Order::toString() const {

  stringstream oss; 
  oss << "Order " << id 
      << ": "     << timestamp << " " << id 
      << " "      << price;
  
  return oss.str();

} // toString()

/**
 * \brief Return timestap of the Order object 
 */
inline trading::Timestamp trading::Order::get_timestamp(void) {
  return timestamp;
}


//
// 5.3 MarketData methods
// 

/**
 * \brief 
 *    Returns a booloean that us true when there is a
 *    message in MarketData that has not been fetched yet
 */
inline bool trading::MarketData::hasNextMessage() {
  return ( cur_ != messages_.end() );
}


/** 
 * \brief
 *   Get the next message (a string representing an order) 
 *   from the vector<string> 
 */
inline const string& trading::MarketData::nextMessage() {

  if ( hasNextMessage() ) {
    vector<string>::iterator next_msg = cur_;
    cur_++;
    return *next_msg;
  } 
  else {
    throw trading::OrderBookError("No message in the input data");
  }
}


/**
 * \brief Get type of the message: 'I' or 'E'
 */
inline char trading::MarketData::get_msg_type(vector<string> & msg_tokens) {

  char msg_type = 'X';

  if ( msg_tokens.size() >= 2 ) {
    msg_type = (msg_tokens[1].c_str())[0];
  }

  return msg_type;
}


/**
 * \brief Tokenize a message string into a vector<string>
 */
inline vector<string> trading::MarketData::tokenize(const string& line, const char& delim) {

  vector<string> msg_tokens;
  istringstream  is(line);
  string         str;

  while ( getline(is, str, delim) ) {
    msg_tokens.push_back(str);
  }
  return msg_tokens;
}

#endif /* ORDERBOOK_H_ */
