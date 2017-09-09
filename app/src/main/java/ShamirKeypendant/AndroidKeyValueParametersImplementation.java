package ShamirKeypendant;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by christoph on 24.08.17.
 */

public class AndroidKeyValueParametersImplementation implements KeyValueParameters {

    public Map<String, String> values;


    public AndroidKeyValueParametersImplementation()
    {}

    public static KeyValueParameters getInstance(){
        AndroidKeyValueParametersImplementation instance = new AndroidKeyValueParametersImplementation();
        instance.values = new HashMap<String,String>();
        return instance;

    }

    @Override
    public void put(String key, String value) {
        values.put(key,value);
    }

    @Override
    public String get(String key) {
        if(values.get(key) != null) {
            return values.get(key);
        }
        return "";
    }
}
