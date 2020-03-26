package com.github.czyzby.autumn.context.impl.method;

import com.github.czyzby.autumn.context.Context;

/**
 * Allows to performs operations on {@link Context} instance.
 * @author MJ
 */
public interface ContextConsumer {
    /**
     * @param context is never null.
     */
    void handleContext(Context context);
}
