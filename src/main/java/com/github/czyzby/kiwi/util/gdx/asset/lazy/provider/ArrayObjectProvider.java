package com.github.czyzby.kiwi.util.gdx.asset.lazy.provider;

import com.badlogic.gdx.utils.Array;

/** Utility implementation of {@link ObjectProvider} that produces regular or typed (when constructed with class object)
 * arrays. Does not rely on reflection.
 *
 * @author MJ
 * @param <Type> type of values stored in the array. */
public class ArrayObjectProvider<Type> implements ObjectProvider<Array<Type>> {
    private final Class<Type> elementType;

    /** Produces regular arrays. Note that the object is stateless and immutable, so one instance per application can be
     * used. */
    public ArrayObjectProvider() {
        elementType = null;
    }

    /** Produces typed arrays.
     *
     * @param elementType type of stored values. */
    public ArrayObjectProvider(final Class<Type> elementType) {
        this.elementType = elementType;
    }

    @Override
    public Array<Type> provide() {
        if (elementType != null) {
            return new Array<Type>(elementType);
        }
        return new Array<Type>();
    }

    /** @return {@link ArrayObjectProvider} producing regular arrays.
     * @param <Type> type of stored values. */
    public static <Type> ArrayObjectProvider<Type> getProvider() {
        return new ArrayObjectProvider<Type>();
    }

    /** @param elementType type of stored values.
     * @return {@link ArrayObjectProvider} producing typed arrays.
     * @param <Type> type of stored values. */
    public static <Type> ArrayObjectProvider<Type> getProvider(final Class<Type> elementType) {
        return new ArrayObjectProvider<Type>(elementType);
    }
}
