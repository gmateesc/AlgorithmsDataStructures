package optravis.patternsearch.dao;

import java.util.List;

/**
 * This class models a data item, which contains three fields: 
 * uid, name, list of keywords
 */

public final class DataItem {

    private final String uid;
    private final String name;
    private final List<String> keywords;

    public DataItem(String uid, String name, List<String> keywords) {
        this.uid = uid;
        this.name = name;
        this.keywords = keywords;
    }

    public String getUid() {
        return uid;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    @Override
    public String toString() {
        return "DataItem{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", keywords=" + keywords +
                '}';
    }
}
