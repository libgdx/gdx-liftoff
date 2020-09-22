package com.github.czyzby.lml.util.collection;

import com.badlogic.gdx.utils.ObjectMap;

/** {@link ObjectMap} that normalizes its keys according to the chosen rule, making it possible to map the same value to
 * multiple keys. An example might be a ignoring-case string map, which maps values according to normalized, lower-case
 * strings and effectively returning the same value for all strings with the same chars when converted to lower-case.
 *
 * @author MJ
 *
 * @param <Key> type of map keys.
 * @param <Value> type of stored values. */
public abstract class KeyNormalizingObjectMap<Key, Value> extends ObjectMap<Key, Value> {
    public KeyNormalizingObjectMap() {
        super();
    }

    public KeyNormalizingObjectMap(final ObjectMap<Key, Value> map) {
        super(map == null ? 32 : map.size); // 32 is default.
        if (map != null) {
            putAll(map);
        }
    }

    @Override
    public <T extends Key> Value get(T key) {
        if (key != null) {
            return super.get(normalizeKey(key));
        }
        return super.get(key);
    }

    @Override
    public Value get(Key key, final Value defaultValue) {
        if (key != null) {
            key = normalizeKey(key);
        }
        return super.get(key, defaultValue);
    }

    @Override
    public Value put(Key key, final Value value) {
        if (key != null) {
            key = normalizeKey(key);
        }
        return super.put(key, value);
    }

    @Override
    public boolean containsKey(Key key) {
        if (key != null) {
            key = normalizeKey(key);
        }
        return super.containsKey(key);
    }

    @Override
    public Value remove(Key key) {
        if (key != null) {
            key = normalizeKey(key);
        }
        return super.remove(key);
    }

    @Override
    public void putAll(ObjectMap<? extends Key, ? extends Value> map) {
        // This matches super behavior, but in case the ObjectMap gets refactored, we still want to delegate putting to
        // put(Key, Value) method to ensure that keys are normalized. Hence the override.
        ensureCapacity(map.size);
        for (Entry<? extends Key, ? extends Value> entry : map) {
            put(entry.key, entry.value);
        }
    }

    /** @param key will be normalized, ensuring that all keys are equal according to the chosen standard.
     * @return normalized key.
     * @throws NullPointerException if key is null. */
    protected abstract Key normalizeKey(final Key key);
}
