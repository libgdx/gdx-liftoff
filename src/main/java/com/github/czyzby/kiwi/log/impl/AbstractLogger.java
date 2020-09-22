package com.github.czyzby.kiwi.log.impl;

import java.util.Date;

import com.github.czyzby.kiwi.log.Logger;
import com.github.czyzby.kiwi.log.LoggerService;
import com.github.czyzby.kiwi.log.formatter.TextFormatter;
import com.github.czyzby.kiwi.util.common.Nullables;

/** Abstract base for Kiwi loggers.
 *
 * @author MJ */
public abstract class AbstractLogger implements Logger {
    protected final LoggerService service;
    private final TextFormatter formatter;
    private final String debugTag, infoTag, errorTag;

    public AbstractLogger(final LoggerService service, final Class<?> forClass) {
        this.service = service;
        formatter = service.getFormatter();
        final String className = service.isUsingSimpleClassNames() ? forClass.getSimpleName() : forClass.getName();
        debugTag = "[DEBUG] " + className;
        infoTag = "[INFO]  " + className;
        errorTag = "[ERROR] " + className;
    }

    @Override
    public void ignore(final Throwable exception) {
    }

    @Override
    public void ignore(final Throwable exception, final String message) {
    }

    @Override
    public void ignore(final Throwable exception, final String message, final Object... arguments) {
    }

    @Override
    public boolean isDebugOn() {
        return service.isDebugOn();
    }

    @Override
    public boolean isInfoOn() {
        return service.isInfoOn();
    }

    @Override
    public boolean isErrorOn() {
        return service.isErrorOn();
    }

    /** @return tag that proceeds debug messages. */
    protected String getDebugTag() {
        return toTag(debugTag);
    }

    /** @return tag that proceeds info messages. */
    public String getInfoTag() {
        return toTag(infoTag);
    }

    /** @return tag that proceeds error messages. */
    public String getErrorTag() {
        return toTag(errorTag);
    }

    private String toTag(final String tag) {
        if (service.isLoggingTime()) {
            return tag + " (" + new Date() + ")";
        }
        return tag;
    }

    @Override
    public void debug(final Object value) {
        if (isDebugOn()) {
            logDebug(getDebugTag(), Nullables.toString(value));
        }
    }

    @Override
    public void debug(final String message) {
        if (isDebugOn()) {
            logDebug(getDebugTag(), message);
        }
    }

    @Override
    public void debug(final String message, final Object... arguments) {
        if (isDebugOn()) {
            logDebug(getDebugTag(), formatter.format(message, arguments));
        }
    }

    @Override
    public void debug(final Throwable exception, final String message) {
        if (isDebugOn()) {
            logDebug(getDebugTag(), message, exception);
        }
    }

    @Override
    public void debug(final Throwable exception, final String message, final Object... arguments) {
        if (isDebugOn()) {
            logDebug(getDebugTag(), formatter.format(message, arguments), exception);
        }
    }

    @Override
    public void info(final Object value) {
        if (isInfoOn()) {
            logInfo(getInfoTag(), Nullables.toString(value));
        }
    }

    @Override
    public void info(final String message) {
        if (isInfoOn()) {
            logInfo(getInfoTag(), message);
        }
    }

    @Override
    public void info(final String message, final Object... arguments) {
        if (isInfoOn()) {
            logInfo(getInfoTag(), formatter.format(message, arguments));
        }
    }

    @Override
    public void info(final Throwable exception, final String message) {
        if (isInfoOn()) {
            logInfo(getInfoTag(), message, exception);
        }
    }

    @Override
    public void info(final Throwable exception, final String message, final Object... arguments) {
        if (isInfoOn()) {
            logInfo(getInfoTag(), formatter.format(message, arguments), exception);
        }
    }

    @Override
    public void error(final Object value) {
        if (isErrorOn()) {
            logError(getErrorTag(), Nullables.toString(value));
        }
    }

    @Override
    public void error(final String message) {
        if (isErrorOn()) {
            logError(getErrorTag(), message);
        }
    }

    @Override
    public void error(final String message, final Object... arguments) {
        if (isErrorOn()) {
            logError(getErrorTag(), formatter.format(message, arguments));
        }
    }

    @Override
    public void error(final Throwable exception, final String message) {
        if (isErrorOn()) {
            logError(getErrorTag(), message, exception);
        }
    }

    @Override
    public void error(final Throwable exception, final String message, final Object... arguments) {
        if (isErrorOn()) {
            logError(getErrorTag(), formatter.format(message, arguments), exception);
        }
    }

    /** @param tag tag of message.
     * @param message should be logged. */
    protected abstract void logDebug(String tag, String message);

    /** @param tag tag of message.
     * @param message should be logged.
     * @param exception cause of the log. */
    protected abstract void logDebug(String tag, String message, Throwable exception);

    /** @param tag tag of message.
     * @param message should be logged. */
    protected abstract void logInfo(String tag, String message);

    /** @param tag tag of message.
     * @param message should be logged.
     * @param exception cause of the log. */
    protected abstract void logInfo(String tag, String message, Throwable exception);

    /** @param tag tag of message.
     * @param message should be logged. */
    protected abstract void logError(String tag, String message);

    /** @param tag tag of message.
     * @param message should be logged.
     * @param exception cause of the log. */
    protected abstract void logError(String tag, String message, Throwable exception);
}
