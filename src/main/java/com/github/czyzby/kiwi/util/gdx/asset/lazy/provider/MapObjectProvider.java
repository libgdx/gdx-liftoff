package com.github.czyzby.kiwi.util.gdx.asset.lazy.provider;

import com.badlogic.gdx.utils.ObjectMap;

/** Utility implementation of {@link ObjectProvider} that produces {@link ObjectMap}s. Does not rely on reflection. Note
 * that the object is stateless and immutable, so one instance per application can be used.
 *
 * @author MJ
 * @param <Key> type of map keys.
 * @param <Value> type of map values. */
public class MapObjectProvider<Key, Value> implements ObjectProvider<ObjectMap<Key, Value>> {
    @Override
    public ObjectMap<Key, Value> provide() {
        return new ObjectMap<Key, Value>();
    }

    /** @param <Key> type of map keys.
     * @param <Value> type of map values.
     * @return {@link MapObjectProvider} that produces new instances of {@link ObjectMap}. */
    public static <Key, Value> MapObjectProvider<Key, Value> getProvider() {
        return new MapObjectProvider<Key, Value>();
    }
}