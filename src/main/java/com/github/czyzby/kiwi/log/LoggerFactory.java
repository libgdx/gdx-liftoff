package com.github.czyzby.kiwi.log;

/** Provides {@link Logger} instances.
 *
 * @author MJ */
public interface LoggerFactory {
    /** @param service contains logger settings. Requests creation of a logger instance.
     * @param forClass requests a logger.
     * @return a new logger for the selected class. */
    Logger newLogger(LoggerService service, Class<?> forClass);
}
