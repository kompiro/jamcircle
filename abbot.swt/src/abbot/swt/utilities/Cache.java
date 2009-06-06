package abbot.swt.utilities;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * A simple abstract cache implementation based on {@link WeakHashMap}.
 * 
 * @author gjohnsto
 */
public abstract class Cache<K, V> {

	private final Map<K, V> map = new WeakHashMap<K, V>();

	/**
	 * Creates a new value based on the specified key.
	 * 
	 * @param key
	 *            the key
	 * @return the new value
	 */
	protected abstract V newValue(K key);

	/**
	 * Gets an object from this cache, creating a new one if necessary.
	 * 
	 * @param key
	 *            the key to use to look up the object.
	 * @return the {@link Object} associated with the specified key
	 */
	public final synchronized V get(K key) {
		V value = map.get(key);
		if (value == null && !map.containsKey(key)) {
			value = newValue(key);
			map.put(key, value);
		}
		return value;
	}

	public final synchronized V peek(K key) {
		return map.get(key);
	}

	public final synchronized V put(K key, V value) {
		return map.put(key, value);
	}

	public final synchronized V remove(K key) {
		return map.remove(key);
	}
}
