package com.github.czyzby.kiwi.util.gdx.asset.lazy.provider;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

/** {@link ObjectProvider} implementation that wraps around a single object and always provides it on every
 * {@link #provide()} call.
 *
 * @author MJ
 *
 * @param <Type> type of singleton object. */
public class SingletonProvider<Type> implements ObjectProvider<Type> {
    private final Type singleton;

    /** Always returns null on {@link #provide()} call. */
    public SingletonProvider() {
        singleton = null;
    }

    /** @param singleton will be always returned by {@link #provide()}. */
    public SingletonProvider(final Type singleton) {
        this.singleton = singleton;
    }

    /** @return {@link ObjectProvider} that always returns null.
     * @param <Type> mock up type of provider. */
    public static <Type> ObjectProvider<Type> empty() {
        return new SingletonProvider<Type>();
    }

    /** @param singleton will be always returned.
     * @return {@link ObjectProvider} that always provides the passed value.
     * @param <Type> type of the singleton. */
    public static <Type> ObjectProvider<Type> of(final Type singleton) {
        return new SingletonProvider<Type>(singleton);
    }

    /** @param singletonType instance of this class will be created using default no-arg constructor with reflection.
     * @return {@link ObjectProvider} that always provides the created instance.
     * @param <Type> type of the singleton.
     * @throws GdxRuntimeException if unable to create instance. */
    public static <Type> ObjectProvider<Type> withType(final Class<Type> singletonType) {
        try {
            return new SingletonProvider<Type>(ClassReflection.newInstance(singletonType));
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException("Unable to create provider.", exception);
        }
    }

    @Override
    public Type provide() {
        return singleton;
    }
}
