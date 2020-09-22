package com.github.czyzby.kiwi.util.common;

/** Provides methods for simple time conversion. Milliseconds (as longs) are often used by Java API-s for scheduling,
 * while seconds (as floats) are used in LibGDX timers and actions.
 *
 * @author MJ */
public class TimeUtilities extends UtilitiesClass {
    public static final long SECOND_IN_MILLIES = 1000L, MINUTE_IN_MILLIES = 60000L, HOUR_IN_MILLIES = 3600000L,
            DAY_IN_MILLIES = 86400000L;
    public static final float MILLISECOND_IN_SECONDS = 0.001f, MINUTE_IN_SECONDS = 60f, HOUR_IN_SECONDS = 3600f,
            DAY_IN_SECONDS = 86400f;

    private TimeUtilities() {
    }

    public static long convertSecondsToMillies(final long seconds) {
        return seconds * SECOND_IN_MILLIES;
    }

    public static long convertMinutesToMillies(final long minutes) {
        return minutes * MINUTE_IN_MILLIES;
    }

    public static long convertHoursToMillies(final long hours) {
        return hours * HOUR_IN_MILLIES;
    }

    public static long convertDaysToMillies(final long days) {
        return days * DAY_IN_MILLIES;
    }

    public static float convertMilliesToSeconds(final float millies) {
        return millies * MILLISECOND_IN_SECONDS;
    }

    public static float convertMinutesToSeconds(final float minutes) {
        return minutes * MINUTE_IN_SECONDS;
    }

    public static float convertHoursToSeconds(final float hours) {
        return hours * HOUR_IN_SECONDS;
    }

    public static float convertDaysToSeconds(final float days) {
        return days * DAY_IN_SECONDS;
    }
}