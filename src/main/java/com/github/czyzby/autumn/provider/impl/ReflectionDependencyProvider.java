package com.github.czyzby.autumn.provider.impl;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.impl.method.MethodInvocation;
import com.github.czyzby.autumn.provider.DependencyProvider;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Turns reflected methods into dependency providers.
 *
 * @author MJ */
public class ReflectionDependencyProvider implements DependencyProvider<Object> {
    private final Context context;
    private final Method providerMethod;
    private final Object methodOwner;

    /** @param context used to provide method dependencies.
     * @param providerMethod provides the selected object type.
     * @param methodOwner used to invoke the method. */
    public ReflectionDependencyProvider(final Context context, final Method providerMethod, final Object methodOwner) {
        this.context = context;
        this.providerMethod = providerMethod;
        this.methodOwner = methodOwner;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Object> getDependencyType() {
        return providerMethod.getReturnType();
    }

    @Override
    public Object provide() {
        try {
            return Reflection.invokeMethod(providerMethod, methodOwner,
                    MethodInvocation.getParametersFromContext(providerMethod.getParameterTypes(), context));
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException("Unable to invoke method: " + providerMethod + " of provider: " + methodOwner,
                    exception);
        }
    }
}
