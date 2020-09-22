package com.github.czyzby.kiwi.log.impl;

import com.github.czyzby.kiwi.log.LoggerService;

/** Logs normally ignored exceptions as debug exceptions.
 *
 * @author MJ */
public class DebugLogger extends DefaultLogger {
    public DebugLogger(final LoggerService service, final Class<?> forClass) {
        super(service, forClass);
    }

    @Override
    public void ignore(final Throwable exception) {
        debug(exception, "Ignored exception.");
    }

    @Override
    public void ignore(final Throwable exception, final String message) {
        debug(exception, message);
    }

    @Override
    public void ignore(final Throwable exception, final String message, final Object... arguments) {
        debug(exception, message, arguments);
    }
}
