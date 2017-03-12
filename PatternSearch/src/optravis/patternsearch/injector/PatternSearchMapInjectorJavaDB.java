package optravis.patternsearch.injector;

import java.util.Map;

import optravis.patternsearch.service.*;
import optravis.patternsearch.client.PatternSearchMapClient;


public class PatternSearchMapInjectorJavaDB<K extends Comparable<? super K>, V> 
    implements PatternSearchMapInjector<K,V>
{
    @Override
	public PatternSearchMapClient<K,V> getClient( Map<K,V> dataItems) {

	PatternSearchMapService<K,V> smap = new PatternSearchMapServiceJavaDB<K,V>( dataItems );
        PatternSearchMapClient<K,V>  res = new PatternSearchMapClient<K,V>( smap );

	return res;       
    }

} // class PatternSearchMapInjectorJavaDB





