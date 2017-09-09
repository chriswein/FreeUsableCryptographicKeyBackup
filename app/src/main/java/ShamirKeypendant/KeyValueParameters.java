package ShamirKeypendant;

/**
 * Created by christoph on 24.08.17.
 */

public interface KeyValueParameters {
    /**
     * Adds a new (key,value) tuple
     * @param key
     * @param value
     */
    void put(String key, String value);

    /**
     * Gets the value of a key. If the key is non existent an empty string will be delivered.
     * @param key
     * @return Value of the key
     */
    String get(String key);
}
