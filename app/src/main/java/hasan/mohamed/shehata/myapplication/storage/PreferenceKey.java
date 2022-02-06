package hasan.mohamed.shehata.myapplication.storage;


/**
 * Created by h on 11/14/2017.
 */

public class PreferenceKey {
    private String key;
    private Object defaultValue;
    public PreferenceKey(String key_, Object defaultValue_){
        key = key_;
        defaultValue = defaultValue_;
    }

    public String getKey() {
        return key;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
