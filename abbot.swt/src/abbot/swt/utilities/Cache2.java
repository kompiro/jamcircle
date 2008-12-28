package abbot.swt.utilities;

/**
 * A simple two-level abstract cache based on {@link Cache}.
 * 
 * @author gjohnsto
 */
public abstract class Cache2<K1, K2, V> extends Cache<K1, Cache<K2, V>> {

	public final synchronized V get(K1 key1, K2 key2) {
		return get(key1).get(key2);
	}

	public final synchronized V peek(K1 key1, K2 key2) {
		Cache<K2, V> c = peek(key1);
		if (c != null)
			return c.peek(key2);
		return null;
	}

	public final synchronized V put(K1 key1, K2 key2, V value) {
		return get(key1).put(key2, value);
	}

	public final synchronized V remove(K1 key1, K2 key2) {
		return get(key1).remove(key2);
	}

	protected abstract V newValue(K1 k1, K2 k2);

	protected final Cache<K2, V> newValue(final K1 k1) {
		return new Cache<K2, V>() {
			protected V newValue(K2 k2) {
				return Cache2.this.newValue(k1, k2);
			}
		};
	}

}
