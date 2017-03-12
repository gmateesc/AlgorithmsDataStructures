/**
 *  This file contains the implementation of the OrderBook 
 *  class and of the related classes Order and MarketData
 *
 *  The OrderBook class maintains outstanding orders. Orders loaded 
 *  from market data messages are of two types: insert and erase.
 *  Outstanding orders are orders that have been inserted but not erased. 
 * 
 *  The OrderBook class provides these public methods 
 *
 *     insert():              insert an order
 *     erase():               erase an order
 *     get_highest_price():   get highest price of outstanding orders
 */

#include "OrderBook.h"

///
/// 1. OrderBook class
/// 

/**
 * \brief Default constructor
 */
trading::OrderBook::OrderBook() {

  ob_Map_  = trading::OrderBook::OrderMultimap();
  ob_Hash_ = trading::OrderBook::OrderHash();

} // OrderBook::OrderBook()


/** 
 * \brief Insert an order in the order book. 
 * Complexity of this method is O(log n): 
 *   o time to do ob_Map_.insert() is O(log n) and 
 *   o time to do ob_Hash_.insert() is O(1) 
 */
void trading::OrderBook::insert (const trading::Order& order) {

  ///
  /// 1. Insert the pair (order.price, order) into the ob_Map_ multimap
  ///
  OrderMultimap::iterator mapIter;
  mapIter = ob_Map_.insert( pair<trading::OrderPrice,trading::Order>(order.price,order) );


  /// 2. Store the (order.id, mapIter) pair in ob_Hash_, where mapIter (which 
  ///    was set above) tells where in ob_Map_ is the pair (order.price, order),  
  ///    so that orders can be quickly found by order.id. 
  ///
  pair<OrderHash::iterator, bool> hashRet; 
  hashRet = ob_Hash_.insert(pair<trading::OrderId, OrderMultimap::iterator>(order.id, mapIter) ); 
  if ( !hashRet.second ) {
    throw OrderBookError("duplicate order ID");
  }


} // OrderBook::insert()


/**
 * \brief 
 *   Erase order from order book the order passed as parameter. 
 *   Complexity of this method is O(1): 
 *     o time to do ob_Hash_,find(order.id) is O(1)
 *     o time to do ob_Map_.erase() is O(1) 
 *     o time to do ob_Hash_.erase() is O(1) 
 */
void trading::OrderBook::erase (const trading::Order& order) {
  
  ///
  ///  Look up the order with order.id in ob_Hash_ and 
  ///  remove the order from ob_Hash_ and ob_Map_:
  ///   
  OrderHash::iterator hmIter = ob_Hash_.find(order.id);
  if ( hmIter != ob_Hash_.end() ) { 

    // mapIter is a ref to the pair (ord.price, ord) 
    // in ob_Map_ that satisfies ord.id == order.id;     
    OrderMultimap::iterator mapIter = hmIter->second;

    // Extract the ord object using mapIter->second
    trading::Order& ord = mapIter->second;    
    
    // Delete order ord from ob_Map_ (multimap) and ob_Hash_ (hashmap)
    ob_Map_.erase(mapIter);        
    ob_Hash_.erase(hmIter);      
  } 
  else {
    throw OrderBookError("Cannot erase non-existent Order");
  }

} // trading::Orderook::erase()


/**
 *  \brief Print all orders in the order book object 
 *  and the highest price of all orders.
 */
string trading::OrderBook::print() {

  stringstream oss;
  trading::Order order;  
  
  for (OrderMultimap::iterator it = ob_Map_.begin(); it != ob_Map_.end(); it++) {
    order = it->second;
    oss << "" << order.price << "\t\t\t// " << order.toString() << endl;
  }
  return oss.str();

} // OrderBook::print()


///
/// 2. Order class implem
/// 

/** 
 * \brief Default constructor
 */
trading::Order::Order() {
  timestamp = 0;
  id        = 0;
  price     = trading::NaN;
}

/**
 * \brief Single-argument constructor: builds an Order from the msg_tokens
 *
 * @param msg_tokens  Tokens from which to build an order:
 *
 *                    for insert: 2000 I 101 44.10
 *                                   0 1   2     3
 *
 *                    for erase:  2100 E 101
 *                                   0 1   2
 */ 
trading::Order::Order(vector<string> & msg_tokens) {

  timestamp = atol( msg_tokens[0].c_str() );
  id        = atol( msg_tokens[2].c_str() );
  if ( msg_tokens.size() >= 4 ) {
    price = atof( msg_tokens[3].c_str() );
  }
  else { 
    price = trading::NaN;
  }

} // trading::Order( vector<string> )



///
/// 3. MarketData methods
/// 

/**
 * \brief 
 *    Read the market data file into the messages_ member of 
 *    MarketData, which has type vector<string>, with one 
 *    vector element for each line in the data file.
 */
void trading::MarketData::getMarketData( ) {

  messages_.clear();
  messages_ = setMessages();  
  cur_ = messages_.begin();

} // MarketData::getMarketData


/** 
 * \brief 
 *   Helper method that converts the data from the data file 
 *   into messages: a vector<string>, each vector element 
 *   containing a message to inserte or erase an order.
 */
vector<string> trading::MarketData::setMessages( ) {
 
  vector<string> messages;  // Messages returned by this method
    
  ifstream is(filename_.c_str());    
  if ( !is ) {
    throw trading::OrderBookError("Bad market data file");
  }
    
  while ( ! is.eof() ) {
    
    string str;
    getline(is, str);
    
    if ( str.length() > 0 ) {
      messages.push_back(str);
    }    
  }
  
  is.close();

  return messages;

} // MarketData::setMessages()
