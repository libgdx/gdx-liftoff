package com.github.czyzby.kiwi.log.impl;

import com.badlogic.gdx.Gdx;
import com.github.czyzby.kiwi.log.Logger;
import com.github.czyzby.kiwi.log.LoggerFactory;
import com.github.czyzby.kiwi.log.LoggerService;

/** Default {@link Logger} implementation. Delegates logger calls directly to current
 * {@link com.badlogic.gdx.Application} instance.
 *
 * @author MJ
 * @see DebugLogger */
public class DefaultLogger extends AbstractLogger {
    public DefaultLogger(final LoggerService service, final Class<?> forClass) {
        super(service, forClass);
    }

    @Override
    protected void logDebug(final String tag, final String message) {
        Gdx.app.debug(tag, message);
    }

    @Override
    protected void logDebug(final String tag, final String message, final Throwable exception) {
        Gdx.app.debug(tag, message, exception);
    }

    @Override
    protected void logInfo(final String tag, final String message) {
        Gdx.app.log(tag, message);
    }

    @Override
    protected void logInfo(final String tag, final String message, final Throwable exception) {
        Gdx.app.log(tag, message, exception);
    }

    @Override
    protected void logError(final String tag, final String message) {
        Gdx.app.error(tag, message);
    }

    @Override
    protected void logError(final String tag, final String message, final Throwable exception) {
        Gdx.app.error(tag, message, exception);
    }

    /** Provides {@link DefaultLogger} instances.
     *
     * @author MJ */
    public static class DefaultLoggerFactory implements LoggerFactory {
        @Override
        public Logger newLogger(final LoggerService service, final Class<?> forClass) {
            return new DefaultLogger(service, forClass);
        }

    }
}
