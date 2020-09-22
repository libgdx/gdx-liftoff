package com.github.czyzby.kiwi.util.gdx.collection;

import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.github.czyzby.kiwi.util.common.UtilitiesClass;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;
import com.github.czyzby.kiwi.util.gdx.collection.disposable.DisposableObjectMap;
import com.github.czyzby.kiwi.util.gdx.collection.immutable.ImmutableObjectMap;
import com.github.czyzby.kiwi.util.gdx.collection.lazy.LazyObjectMap;

/** Common LibGDX maps utilities, somewhat inspired by Guava.
 *
 * @author MJ */
public class GdxMaps extends UtilitiesClass {
    private GdxMaps() {
    }

    /** @return an empty, new {@link ObjectMap}.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    public static <Key, Value> ObjectMap<Key, Value> newObjectMap() {
        return new ObjectMap<Key, Value>();
    }

    /** @param map will be copied.
     * @return a new object map with the passed values.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    public static <Key, Value> ObjectMap<Key, Value> newObjectMap(final ObjectMap<? extends Key, ? extends Value> map) {
        return new ObjectMap<Key, Value>(map);
    }

    /** @param keyAndValues pairs of keys and values. Each value has to be proceeded by a key.
     * @return a new object map with the given values. Not fail-fast - be careful when passing arguments, or it might
     *         result in unexpected map values.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    @SuppressWarnings("unchecked")
    public static <Key, Value> ObjectMap<Key, Value> newObjectMap(final Object... keyAndValues) {
        if (keyAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Total number of keys and values has to be even.");
        }
        final ObjectMap<Key, Value> map = new ObjectMap<Key, Value>();
        for (int pairIndex = 0; pairIndex < keyAndValues.length; pairIndex++) {
            map.put((Key) keyAndValues[pairIndex], (Value) keyAndValues[++pairIndex]);
        }
        return map;
    }

    /** @return an empty, new ordered map.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    public static <Key, Value> OrderedMap<Key, Value> newOrderedMap() {
        return new OrderedMap<Key, Value>();
    }

    /** @param map will be copied.
     * @return a new ordered map with the passed values.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    public static <Key, Value> OrderedMap<Key, Value> newOrderedMap(
            final OrderedMap<? extends Key, ? extends Value> map) {
        return new OrderedMap<Key, Value>(map);
    }

    /** @param keyAndValues pairs of keys and values. Each value has to be proceeded by a key.
     * @return a new ordered map with the given values. Not fail-fast - be careful when passing arguments, or it might
     *         result in unexpected map values.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    @SuppressWarnings("unchecked")
    public static <Key, Value> OrderedMap<Key, Value> newOrderedMap(final Object... keyAndValues) {
        if (keyAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Total number of keys and values has to be even.");
        }
        final OrderedMap<Key, Value> map = new OrderedMap<Key, Value>();
        for (int pairIndex = 0; pairIndex < keyAndValues.length; pairIndex++) {
            map.put((Key) keyAndValues[pairIndex], (Value) keyAndValues[++pairIndex]);
        }
        return map;
    }

    /** @return an empty, new identity map.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    public static <Key, Value> IdentityMap<Key, Value> newIdentityMap() {
        return new IdentityMap<Key, Value>();
    }

    /** @param map will be copied.
     * @return a new identity map with the passed values.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    public static <Key, Value> IdentityMap<Key, Value> newIdentityMap(
            final IdentityMap<? extends Key, ? extends Value> map) {
        IdentityMap<Key, Value> result = new IdentityMap<Key, Value>(map.size);
        result.putAll(map);
        return result;
    }

    /** @param keyAndValues pairs of keys and values. Each value has to be proceeded by a key.
     * @return a new identity map with the given values. Not fail-fast - be careful when passing arguments, or it might
     *         result in unexpected map values.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    @SuppressWarnings("unchecked")
    public static <Key, Value> IdentityMap<Key, Value> newIdentityMap(final Object... keyAndValues) {
        if (keyAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Total number of keys and values has to be even.");
        }
        final IdentityMap<Key, Value> map = new IdentityMap<Key, Value>();
        for (int pairIndex = 0; pairIndex < keyAndValues.length; pairIndex++) {
            map.put((Key) keyAndValues[pairIndex], (Value) keyAndValues[++pairIndex]);
        }
        return map;
    }

    /** @return an empty, new array map.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    public static <Key, Value> ArrayMap<Key, Value> newArrayMap() {
        return new ArrayMap<Key, Value>();
    }

    /** @param map will be copied.
     * @return a new array map with the passed values.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    public static <Key, Value> ArrayMap<Key, Value> newArrayMap(final ArrayMap<? extends Key, ? extends Value> map) {
        return new ArrayMap<Key, Value>(map);
    }

    /** @param ordered if true, map will be ordered.
     * @return an empty, new array map.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    public static <Key, Value> ArrayMap<Key, Value> newArrayMap(final boolean ordered) {
        return new ArrayMap<Key, Value>(ordered, 16); // default capacity
    }

    /** @param keyType class of map keys.
     * @param valueType class of map values.
     * @return an empty, new typed array map.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    public static <Key, Value> ArrayMap<Key, Value> newArrayMap(final Class<Key> keyType,
            final Class<Value> valueType) {
        return new ArrayMap<Key, Value>(keyType, valueType);
    }

    /** @param keyAndValues pairs of keys and values. Each value has to be proceeded by a key.
     * @return a new array map with the given values. Not fail-fast - be careful when passing arguments, or it might
     *         result in unexpected map values.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    @SuppressWarnings("unchecked")
    public static <Key, Value> ArrayMap<Key, Value> newArrayMap(final Object... keyAndValues) {
        if (keyAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Total number of keys and values has to be even.");
        }
        final ArrayMap<Key, Value> map = new ArrayMap<Key, Value>();
        for (int pairIndex = 0; pairIndex < keyAndValues.length; pairIndex++) {
            map.put((Key) keyAndValues[pairIndex], (Value) keyAndValues[++pairIndex]);
        }
        return map;
    }

    /** @param keyType class of map keys.
     * @param valueType class of map values.
     * @param keyAndValues pairs of keys and values. Each value has to be proceeded by a key.
     * @return a new typed array map with the given values. Not fail-fast - be careful when passing arguments, or it
     *         might result in unexpected map values.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    @SuppressWarnings("unchecked")
    public static <Key, Value> ArrayMap<Key, Value> newArrayMap(final Class<Key> keyType, final Class<Value> valueType,
            final Object... keyAndValues) {
        if (keyAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Total number of keys and values has to be even.");
        }
        final ArrayMap<Key, Value> map = new ArrayMap<Key, Value>(keyType, valueType);
        for (int pairIndex = 0; pairIndex < keyAndValues.length; pairIndex++) {
            map.put((Key) keyAndValues[pairIndex], (Value) keyAndValues[++pairIndex]);
        }
        return map;
    }

    /** @param map will be copied.
     * @return a new disposable object map with the passed values.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    public static <Key, Value extends Disposable> DisposableObjectMap<Key, Value> toDisposable(
            final ObjectMap<? extends Key, ? extends Value> map) {
        return new DisposableObjectMap<Key, Value>(map);
    }

    /** @param map will be copied.
     * @return a new immutable map with the passed values.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    public static <Key, Value> ImmutableObjectMap<Key, Value> toImmutable(
            final ObjectMap<? extends Key, ? extends Value> map) {
        return new ImmutableObjectMap<Key, Value>(map);
    }

    /** @param map will be copied.
     * @return a new ordered map with the passed values.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    public static <Key, Value> OrderedMap<Key, Value> toOrdered(final ObjectMap<? extends Key, ? extends Value> map) {
        final OrderedMap<Key, Value> orderedMap = new OrderedMap<Key, Value>();
        for (final ObjectMap.Entry<? extends Key, ? extends Value> entry : map) {
            orderedMap.put(entry.key, entry.value);
        }
        return orderedMap;
    }

    /** @param map will be copied.
     * @param provider creates new object on get(key) calls if the key is not present in the map.
     * @return a new ordered map with the passed values.
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    public static <Key, Value> LazyObjectMap<Key, Value> toLazy(final ObjectMap<? extends Key, ? extends Value> map,
            final ObjectProvider<? extends Value> provider) {
        return new LazyObjectMap<Key, Value>(provider, map);
    }

    /** @param map can be null.
     * @return true if map is null or has no elements. */
    public static boolean isEmpty(final ObjectMap<?, ?> map) {
        return map == null || map.size == 0;
    }

    /** @param map can be null.
     * @return true if map is not null and has at least one element. */
    public static boolean isNotEmpty(final ObjectMap<?, ?> map) {
        return map != null && map.size > 0;
    }

    /** Puts a value with the given key in the passed map, provided that the passed key isn't already present in the
     * map.
     *
     * @param map may contain a value associated with the key.
     * @param key map key.
     * @param value map value to add.
     * @return value associated with the key in the map (recently added or the previous one).
     * @param <Key> type of map keys.
     * @param <Value> type of map values. */
    public static <Key, Value> Value putIfAbsent(final ObjectMap<Key, Value> map, final Key key, final Value value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
            return value;
        }
        return map.get(key);
    }

    /** @param maps will all be cleared, with an additional null-check before the clearing. */
    public static void clearAll(final ObjectMap<?, ?>... maps) {
        for (final ObjectMap<?, ?> map : maps) {
            if (map != null) {
                map.clear();
            }
        }
    }

    /** @param maps all contained maps will all be cleared, with an additional null-check before the clearing. */
    public static void clearAll(final Iterable<ObjectMap<?, ?>> maps) {
        for (final ObjectMap<?, ?> map : maps) {
            if (map != null) {
                map.clear();
            }
        }
    }

    /** Static utility for accessing {@link ObjectMap#size} variable (which is kind of ugly, since it allows to easily
     * modify and damage internal collection data). Performs null check.
     *
     * @param map its size will be checked. Can be null.
     * @return current size of the passed map. 0 if map is empty or null. */
    public static int sizeOf(final ObjectMap<?, ?> map) {
        if (map == null) {
            return 0;
        }
        return map.size;
    }
}
