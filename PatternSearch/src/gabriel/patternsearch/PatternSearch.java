package gabriel.patternsearch;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;


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
	//

	// Method 1: using
	//    import gabriel.patternsearch.service.*;
	// Not very good, because exposes the methods to the service 
	// which may change, requiring changes in all app using the service
	//
	///PatternSearchMapService<String, DataItem> pattern_searcher = 
	///  new PatternSearchMapServiceJavaDB<String, DataItem>(items);


	// Method 2: using
	//    import gabriel.patternsearch.injector.*;
	// Good, because encapsulated the methods of the service in 
	// the injector; a change in servicere quires changomg the injector but not
	// any app using the service

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




    /**
     * Print the items given the list of UIDs
     */
    private static void printResults(Collection<String> uids, Map<String, DataItem> items) {
        for (String uid : uids) {
            System.out.println(items.get(uid));
        }
    }

} // public class PatternSearch { ... }
