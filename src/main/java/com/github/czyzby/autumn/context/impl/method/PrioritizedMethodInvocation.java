package com.github.czyzby.autumn.context.impl.method;

import com.badlogic.gdx.utils.reflect.Method;
import com.github.czyzby.kiwi.util.common.Comparables;

/** Additionally to storing a delayed method invocation, this object contains a priority that allows to sort
 * invocations. Implements {@link Comparable} interface for additional utility.
 *
 * @author MJ */
public class PrioritizedMethodInvocation extends MethodInvocation implements Comparable<PrioritizedMethodInvocation> {
    private final int priority;

    /** @param method will be eventually invoked.
     * @param methodOwner an instance of the object that contains the method.
     * @param parameters will be used to invoke the method.
     * @param priority of the method invocation. */
    public PrioritizedMethodInvocation(final Method method, final Object methodOwner, final Object[] parameters,
            final int priority) {
        super(method, methodOwner, parameters);
        this.priority = priority;
    }

    /** @return priority of this method invocation. */
    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(final PrioritizedMethodInvocation invocation) {
        // Methods with higher priority are executed first.
        return Comparables.normalizeResult(invocation.priority - priority);
    }
}
