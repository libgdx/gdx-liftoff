package com.github.czyzby.kiwi.util.common;

/** Provides static utilities for nullable objects to avoid {@code NullPointerException}s. Equivalent to Java
 * {@code Objects} utility. Java 6 compatible.
 *
 * @author MJ */
public class Nullables extends UtilitiesClass {
    /** "null", matches StringBuilder behavior. */
    public static final String DEFAULT_NULL_STRING = "null";

    private Nullables() {
    }

    /** @param nullable if is null, {@link NullPointerException} is thrown.
     * @throws NullPointerException when passed parameter is null. */
    public static void requireNotNull(final Object nullable) throws NullPointerException {
        if (nullable == null) {
            throw new NullPointerException();
        }
    }

    /** @param nullable can be null.
     * @return true is passed object is null. */
    public static boolean isNull(final Object nullable) {
        return nullable == null;
    }

    /** @param nullable can be null.
     * @return true if passed object is not null. */
    public static boolean isNotNull(final Object nullable) {
        return nullable != null;
    }

    /** @param nullable probable null.
     * @param alternative will be return if nullable is null.
     * @return nullable parameter if it's not null; alternative otherwise.
     * @param <Type> type of passed objects. */
    public static <Type> Type getOrElse(final Type nullable, final Type alternative) {
        return nullable == null ? alternative : nullable;
    }

    /** @param first can be null.
     * @param second can be null.
     * @return true if objects are equal (using equals method) or if both are null. */
    public static boolean areEqual(final Object first, final Object second) {
        return first == second || first != null && first.equals(second);
    }

    /** @param first can be null.
     * @param second can be null.
     * @return true if objects are not equal (using equals method) or one of them is null and the other is not. */
    public static boolean areNotEqual(final Object first, final Object second) {
        if (first == null) {
            return second != null;
        }
        return !first.equals(second);
    }

    /** @param nullables any of these objects can be null.
     * @return true if any of the objects is null. */
    public static boolean isAnyNull(final Object... nullables) {
        for (final Object object : nullables) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }

    /** @param nullables any of these objects can be null.
     * @return true if all passed objects are null. */
    public static boolean areAllNull(final Object... nullables) {
        for (final Object object : nullables) {
            if (object != null) {
                return false;
            }
        }
        return true;
    }

    /** @param nullables any of these objects can be null.
     * @return true if any of the objects is not null. */
    public static boolean isAnyNotNull(final Object... nullables) {
        for (final Object object : nullables) {
            if (object != null) {
                return true;
            }
        }
        return false;
    }

    /** @param nullables any of these objects can be null.
     * @return true if all passed objects are not null. */
    public static boolean areAllNotNull(final Object... nullables) {
        for (final Object object : nullables) {
            if (object == null) {
                return false;
            }
        }
        return true;
    }

    /** @param nullable can be null.
     * @return nullable object converted to string. If parameter is null, "null" string is returned. As long as toString
     *         is properly implemented in the object, this method never returns null. */
    public static String toString(final Object nullable) {
        return nullable == null ? DEFAULT_NULL_STRING : nullable.toString();
    }

    /** @param nullable can be null.
     * @param onNull returned if nullable is null.
     * @return nullable object converted to string. If first parameter is null, onNull parameter is returned. */
    public static String toString(final Object nullable, final String onNull) {
        return nullable == null ? onNull : nullable.toString();
    }

    /** @param nullable might be null.
     * @return nullable object converted to string. If nullable was null, null is returned. Use this method if null is
     *         an acceptable result. */
    public static String toNullableString(final Object nullable) {
        return toString(nullable, null);
    }
}
