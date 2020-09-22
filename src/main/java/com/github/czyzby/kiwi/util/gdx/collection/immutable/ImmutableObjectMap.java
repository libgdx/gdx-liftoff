package com.github.czyzby.kiwi.util.gdx.collection.immutable;

import com.badlogic.gdx.utils.ObjectMap;

/** An unordered map. This implementation is a cuckoo hash map using 3 hashes, random walking, and a small stash for
 * problematic keys. Null keys are not allowed. Null values are allowed. No allocation is done except when growing the
 * table size. <br>
 * <br>
 * This map performs very fast get, containsKey, and remove (typically O(1), worst case O(log(n))). Put may be a bit
 * slower, depending on hash collisions. Load factors greater than 0.91 greatly increase the chances the map will have
 * to rehash to the next higher POT size. <br>
 * <br>
 * Semi-immutable. Contrary to Array, ObjectMap is better encapsulated and ImmutableObjectMap is an (almost) immutable
 * collection. The only way to modify the collection is getting the iterator manually and using its removing methods -
 * original iterators' implementations operate on private fields (rather than public getters/modifying methods) and
 * could not have been rewritten. Still, standard usage of for-each loops is safe. Deprecates and throws
 * UnsupportedOperationExceptions on all operations that mutate the map.
 *
 * @author Nathan Sweet
 * @author MJ */
public class ImmutableObjectMap<Key, Value> extends ObjectMap<Key, Value> {
    @SuppressWarnings("hiding") private final int size;

    /** Creates a new immutable map identical to the specified map.
     *
     * @param map will be copied. */
    public ImmutableObjectMap(final ObjectMap<? extends Key, ? extends Value> map) {
        super(map);
        this.size = super.size;
    }

    /** @param keysAndValues pairs of keys and values.
     * @return a new ImmutableObjectMap created with the passed keys and values.
     * @throws IllegalArgumentException if keys and values total amount is not even.
     * @throws ClassCastException if received unexpected object type.
     * @param <Key> type of map keys.
     * @param <Value> type of set values. */
    @SuppressWarnings("unchecked")
    public static <Key, Value> ImmutableObjectMap<Key, Value> of(final Object... keysAndValues) {
        if (keysAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Keys and values have to be passed in pairs.");
        }
        final ObjectMap<Key, Value> map = new ObjectMap<Key, Value>();
        for (int index = 0; index < keysAndValues.length; index++) {
            map.put((Key) keysAndValues[index], (Value) keysAndValues[++index]);
        }
        return copyOf(map);
    }

    /** @param objectMap will be copied.
     * @return a new ImmutableObjectMap created with the keys and values stored in passed map.
     * @param <Key> type of map keys.
     * @param <Value> type of set values. */
    public static <Key, Value> ImmutableObjectMap<Key, Value> copyOf(
            final ObjectMap<? extends Key, ? extends Value> objectMap) {
        return new ImmutableObjectMap<Key, Value>(objectMap);
    }

    @Override
    @Deprecated
    public Value put(final Key key, final Value value) {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectMap.");
    }

    @Override
    @Deprecated
    public void putAll(ObjectMap<? extends Key, ? extends Value> map) {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectMap.");
    }

    @Override
    @Deprecated
    public Value remove(final Key key) {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectMap.");
    }

    @Override
    @Deprecated
    public void shrink(final int maximumCapacity) {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectMap.");
    }

    @Override
    @Deprecated
    public void clear() {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectMap.");
    }

    @Override
    @Deprecated
    public void clear(final int maximumCapacity) {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectMap.");
    }

    @Override
    @Deprecated
    public void ensureCapacity(final int additionalCapacity) {
        super.ensureCapacity(additionalCapacity);
    }

    /** @return amount of elements in the map. */
    public int size() {
        return size;
    }
}