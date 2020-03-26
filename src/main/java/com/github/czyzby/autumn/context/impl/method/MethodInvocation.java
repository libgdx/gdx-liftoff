package com.github.czyzby.autumn.context.impl.method;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Method;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Utility for delayed method invocations.
 *
 * @author MJ */
public class MethodInvocation {
    private final Method method;
    private final Object methodOwner;
    private final Object[] parameters;

    /** @param method will be eventually invoked.
     * @param methodOwner an instance of the object that contains the method. Will be kept, unless the method is static.
     * @param parameters will be used to invoke the method. */
    public MethodInvocation(final Method method, final Object methodOwner, final Object[] parameters) {
        this.method = method;
        this.methodOwner = method.isStatic() ? null : methodOwner;
        this.parameters = parameters;
    }

    /** @return stored method that will be invoked. */
    public Method getMethod() {
        return method;
    }

    /** @return object that will be used to invoke the stored method. */
    public Object getMethodOwner() {
        return methodOwner;
    }

    /** @return direct reference to arguments that will be used to invoke stored method. */
    public Object[] getParameters() {
        return parameters;
    }

    /** Invokes the stored method with the chosen arguments.
     *
     * @return result of the invoked method. */
    public Object invoke() {
        try {
            return Reflection.invokeMethod(method, methodOwner, parameters);
        } catch (final Exception exception) {
            throw new GdxRuntimeException("Unable to invoke method: " + method.getName() + " of type: " + methodOwner
                    + " with parameters: " + GdxArrays.newArray(parameters), exception);
        }
    }

    /** @param parameterTypes array of types required by method invocation.
     * @param context used to resolve dependencies.
     * @return parameters array. */
    public static Object[] getParametersFromContext(final Class<?>[] parameterTypes, final Context context) {
        if (parameterTypes == null || parameterTypes.length == 0) {
            return Strings.EMPTY_ARRAY;
        }
        final Object[] parameters = new Object[parameterTypes.length];
        for (int index = 0, length = parameterTypes.length; index < length; index++) {
            final Class<?> parameterType = parameterTypes[index];
            parameters[index] = context.provide(parameterType);
        }
        return parameters;
    }
}
