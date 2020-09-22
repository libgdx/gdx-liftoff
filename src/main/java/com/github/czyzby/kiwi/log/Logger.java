package com.github.czyzby.kiwi.log;

/** Common interface for Kiwi loggers.
 *
 * @author MJ
 * @since 1.2
 * @see com.github.czyzby.kiwi.log.impl.DefaultLogger
 * @see com.github.czyzby.kiwi.log.impl.DebugLogger
 * @see com.github.czyzby.kiwi.log.impl.AsynchronousLogger */
public interface Logger {
    /** @return true if debug messages are logged. */
    boolean isDebugOn();

    /** @return true if info messages are logged. */
    boolean isInfoOn();

    /** @return true if error messages are logged. */
    boolean isErrorOn();

    /** By default, this method does nothing. Override if desperate and you want to track exceptions that are normally
     * ignored.
     *
     * @param exception will be ignored. */
    void ignore(Throwable exception);

    /** By default, this method does nothing. Override if desperate and you want to track exceptions that are normally
     * ignored.
     *
     * @param exception will be ignored.
     * @param message optional explanation. Comment equivalent. */
    void ignore(Throwable exception, String message);

    /** By default, this method does nothing. Override if desperate and you want to track exceptions that are normally
     * ignored.
     *
     * @param exception will be ignored.
     * @param message optional explanation. Comment equivalent. Can contain arguments.
     * @param arguments arguments of explanation message. */
    void ignore(Throwable exception, String message, Object... arguments);

    /** @param value will be converted to string and logged on debug level. Can be null. */
    void debug(Object value);

    /** @param message will be logged on debug level. */
    void debug(String message);

    /** @param message will be logged on debug level. Can contain indexed placeholders.
     * @param arguments will replace placeholders.
     * @see com.github.czyzby.kiwi.log.formatter.TextFormatter */
    void debug(String message, Object... arguments);

    /** @param exception cause of the log. Will be logged.
     * @param message will be logged on debug level. */
    void debug(Throwable exception, String message);

    /** @param exception cause of the log. Will be logged.
     * @param message will be logged on debug level. Can contain indexed placeholders.
     * @param arguments will replace placeholders.
     * @see com.github.czyzby.kiwi.log.formatter.TextFormatter */
    void debug(Throwable exception, String message, Object... arguments);

    /** @param value will be converted to string and logged on info level. Can be null. */
    void info(Object value);

    /** @param message will be logged on info level. */
    void info(String message);

    /** @param message will be logged on info level. Can contain indexed placeholders.
     * @param arguments will replace placeholders.
     * @see com.github.czyzby.kiwi.log.formatter.TextFormatter */
    void info(String message, Object... arguments);

    /** @param exception cause of the log. Will be logged.
     * @param message will be logged on info level. */
    void info(Throwable exception, String message);

    /** @param exception cause of the log. Will be logged.
     * @param message will be logged on info level. Can contain indexed placeholders.
     * @param arguments will replace placeholders.
     * @see com.github.czyzby.kiwi.log.formatter.TextFormatter */
    void info(Throwable exception, String message, Object... arguments);

    /** @param value will be converted to string and logged on error level. Can be null. */
    void error(Object value);

    /** @param message will be logged on error level. */
    void error(String message);

    /** @param message will be logged on error level. Can contain indexed placeholders.
     * @param arguments will replace placeholders.
     * @see com.github.czyzby.kiwi.log.formatter.TextFormatter */
    void error(String message, Object... arguments);

    /** @param exception cause of the log. Will be logged.
     * @param message will be logged on error level. */
    void error(Throwable exception, String message);

    /** @param exception cause of the log. Will be logged.
     * @param message will be logged on error level. Can contain indexed placeholders.
     * @param arguments will replace placeholders.
     * @see com.github.czyzby.kiwi.log.formatter.TextFormatter */
    void error(Throwable exception, String message, Object... arguments);
}
