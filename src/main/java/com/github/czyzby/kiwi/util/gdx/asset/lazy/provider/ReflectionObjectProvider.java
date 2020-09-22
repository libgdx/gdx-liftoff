package com.github.czyzby.kiwi.util.gdx.asset.lazy.provider;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

/** A simple implementation of {@link ObjectProvider} that invokes no-argument constructor of passed class on call. For
 * more complex objects providers, you still need custom implementations. Note that the class handled by the provider
 * has to be included for reflection on GWT.
 *
 * @author MJ
 * @param <Type> type of produced values. */
public class ReflectionObjectProvider<Type> implements ObjectProvider<Type> {
    private final Class<Type> type;

    /** @param type will be the type of objects constructed by the provider. Note that the class has to have a public,
     *            no-argument constructor. */
    public ReflectionObjectProvider(final Class<Type> type) {
        this.type = type;
    }

    /** @param type type of produced values.
     * @return a new ReflectionObjectProvider creating new instances of object of the given class.
     * @param <Type> type of produced values. */
    public static <Type> ObjectProvider<Type> forClass(final Class<Type> type) {
        return new ReflectionObjectProvider<Type>(type);
    }

    @Override
    public Type provide() {
        try {
            return ClassReflection.newInstance(type);
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException("Unable to provide object.", exception);
        }
    }
}
