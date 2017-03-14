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


