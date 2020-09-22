package com.github.czyzby.kiwi.util.gdx.preference;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.common.UtilitiesClass;
import com.github.czyzby.kiwi.util.gdx.asset.Asset;

/** Allows to easily access game's {@link Preferences}. Contains cached {@link Preferences} instances to prevent
 * multiple preference loadings on some platforms.
 *
 * @author MJ */
public class ApplicationPreferences extends UtilitiesClass {
    private ApplicationPreferences() {
    }

    /** Caches all accessed preferences. */
    private final static ObjectMap<String, Preferences> PREFERENCES = new ObjectMap<String, Preferences>();
    /** If set, returns preferences with this path. */
    private static String defaultPreferences;

    /** @return default preferences of the application. Note that default preferences had to be set before calling this
     *         method.
     * @see #setDefaultPreferences(String) */
    public static Preferences getPreferences() {
        if (defaultPreferences == null) {
            throw new IllegalStateException("Default preferences path was not set. Cannot access default preferences.");
        }
        return getPreferences(defaultPreferences);
    }

    /** @param preferenceAsset will be set as the default preferences file. Preferences connected with this asset will
     *            be returned by the no parameter method. */
    public static void setDefaultPreferences(final Asset preferenceAsset) {
        setDefaultPreferences(preferenceAsset.getPath());
    }

    /** @param preferencePath will be set as the default preferences file path. Preferences connected with this asset
     *            will be returned by the no parameter method. */
    public static void setDefaultPreferences(final String preferencePath) {
        defaultPreferences = preferencePath;
    }

    /** @param preferencePath ID of the requested preferences. Cannot be empty.
     * @return preferences with the selected path. Will be cached in map - the next access returns the same object. */
    public static Preferences getPreferences(final String preferencePath) {
        if (preferencePath == null) {
            throw new IllegalArgumentException("Path cannot be empty.");
        }
        if (PREFERENCES.containsKey(preferencePath)) {
            return PREFERENCES.get(preferencePath);
        }
        final Preferences preferences = Gdx.app.getPreferences(preferencePath);
        PREFERENCES.put(preferencePath, preferences);
        return preferences;
    }

    /** @param preference contains data of the selected preferences. Cannot be null.
     * @return preferences with the selected path. Will be cached in map - the next access returns the same object. */
    public static Preferences getPreferences(final Asset preference) {
        return getPreferences(preference.getPath());
    }

    /** Saves all currently cached {@link Preferences}. */
    public static void saveAllPreferences() {
        for (final Preferences preferences : PREFERENCES.values()) {
            save(preferences);
        }
    }

    /** @param preferences will be flushed. Can be null. */
    public static void save(final Preferences preferences) {
        if (preferences != null) {
            preferences.flush();
        }
    }
}
