package gabriel.patternsearch.injector;

import java.util.Map;
import gabriel.patternsearch.client.PatternSearchMapClient;


public interface PatternSearchMapInjector<K extends Comparable<? super K>, V> 
{

    public PatternSearchMapClient<K,V> getClient( Map<K,V> dataItems);

} // class PatternSearchMapInjector



