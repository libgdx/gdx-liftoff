package com.github.czyzby.kiwi.util.common;

/** Abstract base for utilities classes with only static methods. Provides a protected constructor which throws an
 * exception on each call. By extending this class with your utilities, you prohibit the users of the class from
 * creating its instance.
 *
 * <p>
 * Note that if you don't provide a private constructor yourself, your utilities class will contain an
 * exception-throwing public constructor by default. This abstract class does not modify the API of your class, it just
 * makes it impossible to create an instance (both manually and with reflection).
 *
 * @author MJ */
public abstract class UtilitiesClass {
    /** @throws IllegalStateException on each call. */
    protected UtilitiesClass() {
        Exceptions.throwUtilitiesConstructionException(getClass());
    }
}
