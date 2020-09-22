package com.github.czyzby.kiwi.log.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.github.czyzby.kiwi.log.Logger;
import com.github.czyzby.kiwi.log.LoggerFactory;
import com.github.czyzby.kiwi.log.LoggerService;

/** Uses an {@link AsyncExecutor} to log messages. Creates a new {@link AsyncTask} for each message. Eventually
 * delegates logging calls to current {@link com.badlogic.gdx.Application}.
 *
 * @author MJ */
public class AsynchronousLogger extends AbstractLogger {
    private final AsyncExecutor executor;

    public AsynchronousLogger(final LoggerService service, final Class<?> forClass) {
        super(service, forClass);
        executor = service.getExecutor();
    }

    @Override
    protected void logDebug(final String tag, final String message) {
        executor.submit(new AsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                Gdx.app.debug(tag, message);
                return null;
            }
        });
    }

    @Override
    protected void logDebug(final String tag, final String message, final Throwable exception) {
        executor.submit(new AsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                Gdx.app.debug(tag, message, exception);
                return null;
            }
        });
    }

    @Override
    protected void logInfo(final String tag, final String message) {
        executor.submit(new AsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                Gdx.app.log(tag, message);
                return null;
            }
        });
    }

    @Override
    protected void logInfo(final String tag, final String message, final Throwable exception) {
        executor.submit(new AsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                Gdx.app.log(tag, message, exception);
                return null;
            }
        });
    }

    @Override
    protected void logError(final String tag, final String message) {
        executor.submit(new AsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                Gdx.app.error(tag, message);
                return null;
            }
        });
    }

    @Override
    protected void logError(final String tag, final String message, final Throwable exception) {
        executor.submit(new AsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                Gdx.app.error(tag, message, exception);
                return null;
            }
        });
    }

    /** Provides {@link AsynchronousLogger} instances.
     *
     * @author MJ */
    public static class AsynchronousLoggerFactory implements LoggerFactory {
        @Override
        public Logger newLogger(final LoggerService service, final Class<?> forClass) {
            return new AsynchronousLogger(service, forClass);
        }
    }
}
