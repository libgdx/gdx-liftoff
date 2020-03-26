package com.github.czyzby.autumn.provider;

import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;

/** Base interface for dependency providers. If an {@link com.github.czyzby.autumn.annotation.Provider}-annotated class
 * implements this interface, it will become a non-reflection-based provider, which might be necessary for most commonly
 * called providers.
 *
 * @author MJ
 *
 * @param <Type> base type of provided objects. */
public interface DependencyProvider<Type> extends ObjectProvider<Type> {
    /** @return base type of provided objects. */
    Class<Type> getDependencyType();

    /** @return an instance of provided object type. Depending on provider type, this might return the same instance or
     *         a new instance on each call. */
    @Override
    Type provide();
}
