package com.github.czyzby.kiwi.util.common;

/** Utility class for comparable classes. Allows to convert compare results into booleans without magic numbers in code.
 *
 * @author MJ */
public class Comparables extends UtilitiesClass {
    /** Standard comparison result returned when the first value is lower than the second. Note: some comparators might
     * return any negative number. */
    public static final int LOWER_THAN_COMPARE_RESULT = -1;
    /** Standard comparison result returned when the compared values are equal. */
    public static final int EQUAL_COMPARE_RESULT = 0;
    /** Standard comparison result returned when the first value is greater than the second. Note: some comparators
     * might return any positive number. */
    public static final int GREATER_THAN_COMPARE_RESULT = 1;

    private Comparables() {
    }

    /** @param compareResult result of a comparison. Assumes that it can return any negative number for lower-than
     *            result and any positive number for greater-than result.
     * @return signum of inverted comparison result (-1, 0 or 1). */
    public static int invertResult(final int compareResult) {
        return normalizeResult(-compareResult);
    }

    /** @param compareResult result of a comparison. Assumes that it can return any negative number for lower-than
     *            result and any positive number for greater-than result.
     * @return signum of comparison result (-1, 0 or 1). */
    public static int normalizeResult(final int compareResult) {
        return compareResult == EQUAL_COMPARE_RESULT ? EQUAL_COMPARE_RESULT
                : compareResult < EQUAL_COMPARE_RESULT ? LOWER_THAN_COMPARE_RESULT : GREATER_THAN_COMPARE_RESULT;
    }

    /** @param compareResult result of a comparison. Assumes that it can return any negative number for lower-than
     *            result and any positive number for greater-than result.
     * @return true if compared values are equal. */
    public static boolean areEqual(final int compareResult) {
        return compareResult == EQUAL_COMPARE_RESULT;
    }

    /** @param compareResult result of a comparison. Assumes that it can return any negative number for lower-than
     *            result and any positive number for greater-than result.
     * @return true if compared values are not equal. */
    public static boolean areNotEqual(final int compareResult) {
        return compareResult != EQUAL_COMPARE_RESULT;
    }

    /** @param compareResult result of a comparison. Assumes that it can return any negative number for lower-than
     *            result and any positive number for greater-than result.
     * @return true if first compared value is lower than the second. Note that it returns proper results for
     *         comparators that return different negative numbers instead of [-1, EQUAL_COMPARE_RESULT, 1]. */
    public static boolean isFirstLowerThanSecond(final int compareResult) {
        return compareResult < EQUAL_COMPARE_RESULT;
    }

    /** @param compareResult result of a comparison. Assumes that it can return any negative number for lower-than
     *            result and any positive number for greater-than result.
     * @return true if first compared value is lower than or equal to the second. Note that it returns proper results
     *         for comparators that return different negative numbers instead of [-1, EQUAL_COMPARE_RESULT, 1] */
    public static boolean isFirstLowerOrEqualToSecond(final int compareResult) {
        return compareResult <= EQUAL_COMPARE_RESULT;
    }

    /** @param compareResult result of a comparison. Assumes that it can return any negative number for lower-than
     *            result and any positive number for greater-than result.
     * @return true if first compared value is greater than the second. Note that it returns proper results for
     *         comparators that return different positive numbers instead of [-1, EQUAL_COMPARE_RESULT, 1]. */
    public static boolean isFirstGreaterThanSecond(final int compareResult) {
        return compareResult > EQUAL_COMPARE_RESULT;
    }

    /** @param compareResult result of a comparison. Assumes that it can return any negative number for lower-than
     *            result and any positive number for greater-than result.
     * @return true if first compared value is greater than or equal to the second. Note that it returns proper results
     *         for comparators that return different positive numbers instead of [-1, EQUAL_COMPARE_RESULT, 1] */
    public static boolean isFirstGreaterOrEqualToSecond(final int compareResult) {
        return compareResult >= EQUAL_COMPARE_RESULT;
    }

    /** Safely compares two values that might be null. Null value is considered lower than non-null, even if the
     * non-null value is minimal in its range.
     *
     * @param first first compared value.
     * @param second second compared value.
     * @return comparison result of first and second value.
     * @param <Value> common values type. */
    public static <Value extends Comparable<Value>> int nullSafeCompare(final Value first, final Value second) {
        if (first == null) {
            return second == null ? EQUAL_COMPARE_RESULT : LOWER_THAN_COMPARE_RESULT;
        }
        return second == null ? GREATER_THAN_COMPARE_RESULT : first.compareTo(second);
    }
}
