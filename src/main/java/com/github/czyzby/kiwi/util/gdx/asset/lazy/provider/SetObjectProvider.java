package com.github.czyzby.kiwi.util.gdx.asset.lazy.provider;

import com.badlogic.gdx.utils.ObjectSet;

/** Utility implementation of {@link ObjectProvider} that produces {@link ObjectSet}s. Does not rely on reflection. Note
 * that the object is stateless and immutable, so one instance per application can be used.
 *
 * @author MJ
 * @param <Type> type of stored values. */
public class SetObjectProvider<Type> implements ObjectProvider<ObjectSet<Type>> {
    @Override
    public ObjectSet<Type> provide() {
        return new ObjectSet<Type>();
    }

    /** @return {@link SetObjectProvider} providing new {@link ObjectSet} instances.
     * @param <Type> type of stored values. */
    public static <Type> SetObjectProvider<Type> getProvider() {
        return new SetObjectProvider<Type>();
    }
}