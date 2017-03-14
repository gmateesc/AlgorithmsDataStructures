
# Pattern search in the entries of a Java map


## Table of Contents

- [The Problem](#p1)

- [Description of the solution](#p2)
  - [The service interface and its implementation](#p21)
  - [Decoupling the application from the service implementation](#p22)
  - [Generating the data items](#p23)
  - [Putting it all together: the PatternSearch application](#p24)


- [Running the code](#p3)



<a name="p1" id="p1"> </a>
## The Problem Solved 

Consider the following problem: the input is a map of 1000000 data items. Each item has three fields: a name of type String, a unique ID (uid) field of type String, and a keywords field of type List<String>. 
The problem is to write a Java program that searches for a given string search_string in the keywords of all data items, where the search is case insensitive and search_string, matches sub-string of the strings in keywords, and return a collection of uids of data items that match the search string. 

The solution must meet the following requirements:
* Search must be case insensitive and include partial matches (the search_string can be a sub-string of a keyword in the list of keywords)
* The result must not contain duplicate uids
* The search time should be as small as possible and be reported by the program



<a name="p2" id="p2"> </a>
## Description of the solution

The problem is essentuially to peform a pattern search in a Java map Map<K, V>, 
where V is a java type that has a String field. agains which the pattern 
match is performed. Return the Map entries matching the search. 

To make the pattern search fast, I chose to implement it using JDBC against 
an in-memory table in which I loaded the data items.


The solution uses two main software design techniques:

* the dependency injection design pattern, see [this page](https://github.com/gmateesc/DesignPatterns/tree/master/Factory_DependencyInjection#p2), in order to allow supporting different implementations of the pattern seach service;

* Java generics: in order to support using the code fo searching other collections of Java objects.


<a name="p21" id="p21"></a>
### The service interface and its implementation 

I defined a service [interface](https://github.com/gmateesc/AlgorithmsDataStructures/blob/master/PatternSearch/src/gabriel/patternsearch/service/PatternSearchMapService.java): 
```
gabriel.patternsearch.service.PatternSearchMapService<K,V>
``` 

that specfies the service that peforms the search for the search pattern in the Map entries:  

```
package gabriel.patternsearch.service;

import java.util.Collection;
import java.util.ArrayList;


public interface PatternSearchMapService<K extends Comparable<? super K>, V>
{
    public Collection<String> searchForPattern(String searchPattern);

} 
```


Then I created an [implementation](https://github.com/gmateesc/AlgorithmsDataStructures/blob/master/PatternSearch/src/gabriel/patternsearch/service/PatternSearchMapServiceJavaDB.java) of the service PatternSearchMapService using the class:

```
gabriel.patternsearch.service.PatternSearchMapServiceJavaDB<K.V>
``` 
This class implements the searchForPattern() method by loading the Map entries into a relational 
table of an embeeded Derby Database and using JDBC to peform pattern search against the table:

```
package gabriel.patternsearch.service;

import java.util.*;
import java.sql.*;

import gabriel.patternsearch.dao.DataItem;


public class 
    PatternSearchMapServiceJavaDB<K extends Comparable<? super K>, V> implements PatternSearchMapService<K,V> 
{


 @Override 
      public Collection<String> searchForPattern(String searchPattern) {

       ...

    } // searchForPattern

}
```



<a name="p22" id="p22"></a>
### Decoupling the application from the service implementation 

I used the dependency injection pattern to allow applications to use other 
implementations of the pattern search servuce than the class 
_PatternSearchMapServiceJavaDB<K,V>_ that I provided.


I implemented dependency injection pattern using: 
* a client class, 
* an injector interface, and 
* an injector class


#### The client class

```
gabriel.patternsearch.client.PatternSearchMapClient<K, V>
```
is used by applications to access an implementation of the PatternSearchMapService interface. 

Using the client class allows applications to always invoke the method 
_searchForPattern()_ on a client class object. even if the service implementation 
changes or a new service implementation is added:

```
package gabriel.patternsearch.client;

import java.util.Collection;
import gabriel.patternsearch.service.*;

public class PatternSearchMapClient<K extends Comparable<? super K>, V>
{

    private PatternSearchMapService<K,V> service;
    
    public PatternSearchMapClient( PatternSearchMapService<K,V> svc ) {
    this.service = svc;
    }


    public Collection<String> searchForPattern(String searchPattern) {

        // delegate searching for pattern to the service object 
        Collection<String> res = this.service.searchForPattern(searchPattern);
	return res;
    }

} // class PatternSearchMapClient

``` 


#### The injector interface and injector class

An application obtains an instance of the client class using an object of type 
injector class, where the inject class that implements the injector interface. 


The [injector interface](https://github.com/gmateesc/AlgorithmsDataStructures/blob/master/PatternSearch/src/gabriel/patternsearch/injector/PatternSearchMapInjector.java) defines the method _getClient()_ that needs to be implemented by any injector class:

```
package gabriel.patternsearch.injector;

import java.util.Map;
import gabriel.patternsearch.client.PatternSearchMapClient;

public interface PatternSearchMapInjector<K extends Comparable<? super K>, V> 
{
    public PatternSearchMapClient<K,V> getClient( Map<K,V> dataItems);
} 
```


I defined an [injector class](https://github.com/gmateesc/AlgorithmsDataStructures/blob/master/PatternSearch/src/gabriel/patternsearch/injector/PatternSearchMapInjectorJavaDB.java) that implements the method _getClient()_ in terms of the class PatternSearchMapServiceJavaDB<K,V> (which implements the service interface PatternSearchMapService<K,V>):

```
package gabriel.patternsearch.injector;

import java.util.Map;
import gabriel.patternsearch.service.*;
import gabriel.patternsearch.client.PatternSearchMapClient;

public class PatternSearchMapInjectorJavaDB<K extends Comparable<? super K>, V> 
    implements PatternSearchMapInjector<K,V>
{
    @Override
    public PatternSearchMapClient<K,V> getClient( Map<K,V> dataItems) {

    PatternSearchMapService<K,V> smap = new PatternSearchMapServiceJavaDB<K,V>( dataItems );
        PatternSearchMapClient<K,V>  res = new PatternSearchMapClient<K,V>( smap );
	return res;       
    }
} 
```



<a name="p23" id="p23"></a>
### Generating the data items


The map of 1000000 data items is generated by the class [package gabriel.patternsearch.dao.DataItemsDAO]
(https://github.com/gmateesc/AlgorithmsDataStructures/blob/master/PatternSearch/src/gabriel/patternsearch/dao/DataItemsDAO.java) using the _getDataItems()_ static method:


```
package gabriel.patternsearch.dao;

import java.util.*;
import gabriel.patternsearch.dao.DataItem;

/**
 * Class that generates a map of numberOfItems data items, with the map key 
 * being UID_<ITEM_NUMBER>
 */
public final class DataItemsDAO {

    public static Map<String, DataItem> getDataItems(int numberOfItems) {

        // Array of country name abbreviations
        String[] countries = Locale.getISOCountries();

        // Country to language map
        Map<String, String> country_2_language = getCountry2LanguageMap();

        // Build the items map
        Map<String, DataItem> items = new HashMap<>();
        Random random = new Random();

        for (int i = 0; i < numberOfItems; i++) {

   	    //
	    // 1. Fill in the fields of a data item 
	    //
            String uid = "UID_" + i;
            String name = "Item " + i;

            // pick a country randomly
            String countryCode = countries[random.nextInt(countries.length)];

            Locale locale = new Locale
	    	   (
		    country_2_language.containsKey(countryCode) ? country_2_language.get(countryCode) : "",
		    countryCode
		   );

            List<String> keywords = Arrays.asList( 
                                     locale.getCountry(), 
                                     locale.getDisplayCountry(Locale.ENGLISH) 
                                 );

	    //
            // 2. Add a data item to the map
            //
            items.put(uid, new DataItem(uid, name, keywords));
        }

        return items;
    }

    // ...
}
```






<a name="p24" id="p24"></a>
### Putting it all together: the PatternSearch application


Once we have defined the interfaces and classes described above, the application that performs the pattern search on the java objects will be easy to write. 


The class ![gabriel.patternsearch.PatternSearch](https://github.com/gmateesc/AlgorithmsDataStructures/blob/master/PatternSearch/src/gabriel/patternsearch/PatternSearch.java) does the work in the _main()_ method:

```
package gabriel.patternsearch;

import java.util.*;

import gabriel.patternsearch.injector.*;
import gabriel.patternsearch.client.*;
import gabriel.patternsearch.dao.*;


public class PatternSearch {

    // Pattern to search for
    private static final String SEARCH_PATTERN = "swi";

    // number of data items to be searched
    private static final int NUMBER_OF_ITEMS = 1000000;


    /** 
     * Search the data using the PatternSearchService
     */
    public static void main(String[] args) {

        System.out.println("Number of items: " + NUMBER_OF_ITEMS);
        System.out.println("Search pattern: " + SEARCH_PATTERN);

	//
	// 1. Get the data items as a map 
	//
        Map<String, DataItem> items = DataItemsDAO.getDataItems(NUMBER_OF_ITEMS);


	//
	// 2. Create a PatternSearchMapClient object and then 
	//    use it for invking the searcjForPattern() method
	PatternSearchMapClient<String, DataItem> pattern_searcher = 
	    (new PatternSearchMapInjectorJavaDB<String, DataItem>()).getClient(items);


	//
	// 3. Search for the pattern SEARCH_PATTERN in the smap entries
	//
        Collection<String> uids = pattern_searcher.searchForPattern(SEARCH_PATTERN);


	//
	// 4. Show the results of the search
	//
        System.out.println("Results found: " + uids.size());
        printResults(uids, items);

    } // main()


    // ...
} 

```

The collection of DataItem objects to be searched are loaded into the 
_items_ object of type _Map<String, DataItem>_, then the injector is 
used to get an object _pattern_searcher_ which is and instance of the 
_PatternSearchMapClient<String, DataItem>_ class, and finally 
the method  _searchForPattern(SEARCH_PATTERN)_ is invoked on 
the +pattern_searcher_ object to get the list UIDs.







<a name="p3" id="p3"> </a>
## Running the code

#### To build and run the pattern search application, enter at the shell
```
  ant
```

#### To clean-build and run the pattern search application, enter at the shell
```
  ant clean main 
```

#### To run the search engine with sample data:
```
  ant engine
```


Below is a sample run of the application: 
```
  $ ant

  Buildfile: /Users/gabriel/GitHub/maven-projects/PatternSearchJava/build.xml

  compile:
    [mkdir] Created dir: /Users/gabriel/GitHub/maven-projects/PatternSearchJava/classes
    [javac] Compiling 8 source files to /Users/gabriel/GitHub/maven-projects/PatternSearchJava/classes

  main:
     [java] Number of items: 1000000
     [java] Search pattern: swi
     [java] The search took 1.515 seconds
     [java] Results found: 3925
     [java] DataItem{uid='UID_100374', name='Item 100374', keywords=[CH, Switzerland]}
     [java] DataItem{uid='UID_100394', name='Item 100394', keywords=[CH, Switzerland]}
     ...
     [java] DataItem{uid='UID_999523', name='Item 999523', keywords=[CH, Switzerland]}

  BUILD SUCCESSFUL
  Total time: 23 seconds

```




