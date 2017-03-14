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
	    //
	    // 2. Add a data item to the mao
	    //
            items.put(uid, new DataItem(uid, name, keywords));
        }

        return items;
    }


    private static Map<String, String> getCountry2LanguageMap() {

        Map<String, String> country_2_language = new HashMap<>();

        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            if ((locale.getDisplayCountry() != null) && (!"".equals(locale.getDisplayCountry()))) {
                country_2_language.put(locale.getCountry(), locale.getLanguage());
            }
        }
        return country_2_language;
    }
}
