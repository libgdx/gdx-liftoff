package com.github.czyzby.lml.parser.impl.action;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Method;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.util.LmlUtilities;

/** Wraps around a reflected method, implementing actor consumer interface. Invokes the wrapped method on action usage.
 *
 * @author MJ */
public class MethodActorConsumer implements ActorConsumer<Object, Object> {
    private final Method method;
    private final Object methodOwner;
    private final Object[] arguments;

    public MethodActorConsumer(final Method method, final Object methodOwner) {
        if (method == null || methodOwner == null) {
            throw new IllegalArgumentException(
                    "Method actor consumer has to wrap around an existing method and its owner.");
        }
        this.method = method;
        this.methodOwner = methodOwner;
        final Class<?>[] parameters = method.getParameterTypes();
        if (parameters == null || parameters.length == 0) {
            arguments = LmlUtilities.EMPTY_ARRAY;
        } else {
            arguments = new Object[1];
        }
    }

    @Override
    public Object consume(final Object actor) {
        if (containsArgument()) {
            arguments[0] = actor;
        }
        try {
            return Reflection.invokeMethod(method, methodOwner, arguments);
        } catch (final Exception exception) {
            throw new GdxRuntimeException("Unable to invoke method: " + method + " of object: " + methodOwner
                    + (containsArgument() ? " with argument: " + actor : ""), exception);
        }
    }

    private boolean containsArgument() {
        return arguments.length == 1;
    }
}
