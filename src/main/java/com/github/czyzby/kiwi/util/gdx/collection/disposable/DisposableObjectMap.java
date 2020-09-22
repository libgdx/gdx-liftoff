package com.github.czyzby.kiwi.util.gdx.collection.disposable;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

/** An unordered map. This implementation is a cuckoo hash map using 3 hashes, random walking, and a small stash for
 * problematic keys. Null keys are not allowed. Null values are allowed. No allocation is done except when growing the
 * table size. <br>
 * <br>
 * This map performs very fast get, containsKey, and remove (typically O(1), worst case O(log(n))). Put may be a bit
 * slower, depending on hash collisions. Load factors greater than 0.91 greatly increase the chances the map will have
 * to rehash to the next higher POT size. <br>
 * <br>
 * Utility container for disposable objects.
 *
 * @author Nathan Sweet
 * @author MJ */
public class DisposableObjectMap<Key, Value extends Disposable> extends ObjectMap<Key, Value>implements Disposable {
    /** Creates a new map with an initial capacity of 32 and a load factor of 0.8. This map will hold 25 items before
     * growing the backing table. */
    public DisposableObjectMap() {
        super();
    }

    /** Creates a new map with a load factor of 0.8. This map will hold initialCapacity * 0.8 items before growing the
     * backing table.
     *
     * @param initialCapacity initial expected amount of elements. */
    public DisposableObjectMap(final int initialCapacity) {
        super(initialCapacity);
    }

    /** Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity *
     * loadFactor items before growing the backing table.
     *
     * @param initialCapacity initial expected amount of elements.
     * @param loadFactor determines when the map is grown. */
    public DisposableObjectMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /** Creates a new disposable map identical to the specified map.
     *
     * @param map will be copied. */
    public DisposableObjectMap(final ObjectMap<? extends Key, ? extends Value> map) {
        super(map);
    }

    /** @return new disposable map instance.
     * @param <Key> type of map keys.
     * @param <Value> type of set values. */
    public static <Key, Value extends Disposable> DisposableObjectMap<Key, Value> newMap() {
        return new DisposableObjectMap<Key, Value>();
    }

    /** @param keysAndValues pairs of keys and values.
     * @return a new DisposableObjectMap created with the passed keys and values.
     * @throws IllegalArgumentException if keys and values total amount is not even.
     * @throws ClassCastException if received unexpected object type.
     * @param <Key> type of map keys.
     * @param <Value> type of set values. */
    @SuppressWarnings("unchecked")
    public static <Key, Value extends Disposable> DisposableObjectMap<Key, Value> of(final Object... keysAndValues) {
        if (keysAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Keys and values have to be passed in pairs.");
        }
        final DisposableObjectMap<Key, Value> map = new DisposableObjectMap<Key, Value>();
        for (int index = 0; index < keysAndValues.length; index += 2) {
            map.put((Key) keysAndValues[index], (Value) keysAndValues[index + 1]);
        }
        return map;
    }

    /** @param objectMap will be copied.
     * @return a new DisposableObjectMap created with the keys and values stored in passed map.
     * @param <Key> type of map keys.
     * @param <Value> type of set values. */
    public static <Key, Value extends Disposable> DisposableObjectMap<Key, Value> copyOf(
            final ObjectMap<? extends Key, ? extends Value> objectMap) {
        return new DisposableObjectMap<Key, Value>(objectMap);
    }

    /** @return current amount of elements in the map. */
    public int size() {
        return super.size;
    }

    @Override
    public void dispose() {
        for (final Disposable disposable : values()) {
            if (disposable != null) {
                disposable.dispose();
            }
        }
    }
}