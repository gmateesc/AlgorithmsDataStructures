package optravis.patternsearch.service;

import java.util.Collection;
import java.util.ArrayList;


public interface PatternSearchMapService<K extends Comparable<? super K>, V>
{

    /**
     * Find the map entries which match searchPattern and return the uids of those 
     * items as a Collection<String>
     *
     * - Search is performed on table into which Java Map entries are loaded
     * - Search is case insensitive and covers sub-strings (partial match)
     * - Results does not contain duplicate uids
     *
     * @param searchPattern a string to search for in data items keywords
     * @return a collection of uids of data items which match the search criteria
     */
    public Collection<String> searchForPattern(String searchPattern);


} // class PatternSearchMapService<K,V>


