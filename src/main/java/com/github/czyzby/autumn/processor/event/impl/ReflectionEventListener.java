package com.github.czyzby.autumn.processor.event.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Method;
import com.github.czyzby.autumn.annotation.OnEvent;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.impl.method.MethodInvocation;
import com.github.czyzby.autumn.processor.event.EventListener;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;

/** Wraps around a reflected method, turning it into a listener.
 *
 * @author MJ */
public class ReflectionEventListener extends MethodInvocation implements EventListener<Object> {
    private final boolean removeAfterInvocation;
    private final boolean strict;
    private final ObjectProvider<?>[] providers;

    public ReflectionEventListener(final Method method, final Object methodOwner, final Context context,
            final boolean removeAfterInvocation, final boolean strict) {
        super(method, methodOwner, getParameters(method, context));
        this.removeAfterInvocation = removeAfterInvocation;
        this.strict = strict;
        providers = getProviders(method, context);
    }

    private static Object[] getParameters(final Method method, final Context context) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes == null || parameterTypes.length == 0) {
            return Strings.EMPTY_ARRAY;
        }
        final Object[] parameters = new Object[parameterTypes.length];
        for (int index = 0, length = parameterTypes.length; index < length; index++) {
            final Class<?> parameterType = parameterTypes[index];
            if (context.isPresent(parameterType)) {
                parameters[index] = context.getComponent(parameterType);
            }
        }
        return parameters;
    }

    private static ObjectProvider<?>[] getProviders(final Method method, final Context context) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes == null || parameterTypes.length == 0) {
            return new ObjectProvider<?>[0];
        }
        final ObjectProvider<?>[] providers = new ObjectProvider<?>[parameterTypes.length];
        for (int index = 0, length = parameterTypes.length; index < length; index++) {
            final Class<?> parameterType = parameterTypes[index];
            if (context.isProviderPresentFor(parameterType)) {
                providers[index] = context.getProvider(parameterType);
            }
        }
        return providers;
    }

    @Override
    public boolean processEvent(final Object event) {
        replaceParameters(event);
        try {
            final Object result = invoke();
            if (removeAfterInvocation) {
                return OnEvent.REMOVE;
            } else if (result instanceof Boolean) {
                return ((Boolean) result).booleanValue();
            }
            return OnEvent.KEEP;
        } catch (final Exception exception) {
            if (strict) {
                // Gdx applications seem to ignore exceptions in posted runnables. This is bad.
                Gdx.app.error("ERROR", "Exception occured on event listener.", exception);
                throw new GdxRuntimeException("Unable to invoke event listener.", exception);
            }
            return OnEvent.KEEP;
        }
    }

    private void replaceParameters(final Object event) {
        final Class<?>[] parameterTypes = getMethod().getParameterTypes();
        if (parameterTypes == null || parameterTypes.length == 0) {
            return;
        }
        final Object[] parameters = getParameters();
        final Class<?> eventType = event.getClass();
        for (int index = 0, length = parameterTypes.length; index < length; index++) {
            if (eventType.equals(parameterTypes[index])) {
                parameters[index] = event;
            } else if (providers[index] != null) {
                parameters[index] = providers[index].provide();
            }
        }
    }
}
