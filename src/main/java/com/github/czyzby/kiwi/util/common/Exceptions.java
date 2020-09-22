package com.github.czyzby.kiwi.util.common;

/** Common exception utilities. Note: if desperate, you can locally override this class to handle all ignored
 * exceptions.
 *
 * @author MJ */
public class Exceptions extends UtilitiesClass {
    private Exceptions() {
    }

    /** Allows to turn empty catch blocks into somewhat documented exception ignoring blocks.
     *
     * @param exception will be ignored. */
    public static void ignore(final Throwable exception) {
    }

    /** Allows to turn empty catch blocks into somewhat documented exception ignoring blocks.
     *
     * @param reason optional reason why the exception was ignored for code clarity.
     * @param exception will be ignored. */
    public static void ignore(final String reason, final Throwable exception) {
    }

    /** @param exception will be converted to {@link RuntimeException} (or casted).
     * @return a new runtime exception or passed exception. */
    public static RuntimeException toRuntimeException(final Throwable exception) {
        if (exception instanceof RuntimeException) {
            return (RuntimeException) exception;
        }
        return new RuntimeException(exception);
    }

    /** @param exception will be converted to {@link RuntimeException}.
     * @param message new exception message.
     * @return a new runtime exception. */
    public static RuntimeException toRuntimeException(final String message, final Throwable exception) {
        return new RuntimeException(message, exception);
    }

    /** @param exception will be converted to {@link RuntimeException} and thrown. */
    public static void throwRuntimeException(final Throwable exception) {
        throw toRuntimeException(exception);
    }

    /** @param exception will be converted to {@link RuntimeException} and thrown.
     * @param message optional new exception message. */
    public static void throwRuntimeException(final String message, final Throwable exception) {
        throw toRuntimeException(message, exception);
    }

    /** Helper method for utility classes. Usually, classes with only static methods are prohibited from being initiated
     * by including a private constructor, but an instance of the class might still be created with reflection
     * (sometimes by a mistake). By invoking this method in the private constructor, instance of the class will not be
     * possible to create in normal conditions.
     *
     * @throws IllegalStateException on each invocation. */
    public static void throwUtilitiesConstructionException() {
        throw new IllegalStateException(
                "Instance of this class should not be constructed. Use static methods instead.");
    }

    /** Helper method for utility classes. Usually, classes with only static methods are prohibited from being initiated
     * by including a private constructor, but an instance of the class might still be created with reflection
     * (sometimes by a mistake). By invoking this method in the private constructor, instance of the class will not be
     * possible to create in normal conditions.
     *
     * @param utilitiesClass is a utilities class and should not be constructed.
     * @throws IllegalStateException on each invocation. */
    public static void throwUtilitiesConstructionException(final Class<?> utilitiesClass) {
        throw new IllegalStateException(
                "Instance of " + utilitiesClass + " should not be constructed. Use static methods instead.");
    }
}
