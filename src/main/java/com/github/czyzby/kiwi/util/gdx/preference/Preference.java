package com.github.czyzby.kiwi.util.gdx.preference;

import com.badlogic.gdx.Preferences;

/** A simple interface for a single game preference. Advised to be implemented by an enum holding all relevant game
 * preferences.
 *
 * @author MJ */
public interface Preference {
    /** @return name of the preference. Used as key in the preference map. */
    public String getName();

    /** @param preferences will contain the preference.
     * @param preferenceValue will be set as the preference value. */
    public void setIn(Preferences preferences, String preferenceValue);

    /** @param preferences will contain the preference.
     * @param preferenceValue will be set as the preference value. */
    public void setIn(Preferences preferences, boolean preferenceValue);

    /** @param preferences will contain the preference.
     * @param preferenceValue will be set as the preference value. */
    public void setIn(Preferences preferences, int preferenceValue);

    /** @param preferences will contain the preference.
     * @param preferenceValue will be set as the preference value. */
    public void setIn(Preferences preferences, long preferenceValue);

    /** @param preferences will contain the preference.
     * @param preferenceValue will be set as the preference value. */
    public void setIn(Preferences preferences, float preferenceValue);

    /** @param preferences must contain the parameter.
     * @return the value connected with the preference's key present in the passed preferences. */
    public String getStringFrom(Preferences preferences);

    /** @param preferences can contain the parameter.
     * @param defaultValue returned if preference is not found.
     * @return the value connected with the preference's key present in the passed preferences or passed default value
     *         if not found. */
    public String getStringOrElse(Preferences preferences, String defaultValue);

    /** @param preferences must contain the parameter.
     * @return the value connected with the preference's key present in the passed preferences. */
    public boolean getBooleanFrom(Preferences preferences);

    /** @param preferences can contain the parameter.
     * @param defaultValue returned if preference is not found.
     * @return the value connected with the preference's key present in the passed preferences or passed default value
     *         if not found. */
    public boolean getBooleanOrElse(Preferences preferences, boolean defaultValue);

    /** @param preferences must contain the parameter.
     * @return the value connected with the preference's key present in the passed preferences. */
    public int getIntFrom(Preferences preferences);

    /** @param preferences can contain the parameter.
     * @param defaultValue returned if preference is not found.
     * @return the value connected with the preference's key present in the passed preferences or passed default value
     *         if not found. */
    public int getIntOrElse(Preferences preferences, int defaultValue);

    /** @param preferences must contain the parameter.
     * @return the value connected with the preference's key present in the passed preferences. */
    public long getLongFrom(Preferences preferences);

    /** @param preferences can contain the parameter.
     * @param defaultValue returned if preference is not found.
     * @return the value connected with the preference's key present in the passed preferences or passed default value
     *         if not found. */
    public long getLongOrElse(Preferences preferences, long defaultValue);

    /** @param preferences must contain the parameter.
     * @return the value connected with the preference's key present in the passed preferences. */
    public float getFloatFrom(Preferences preferences);

    /** @param preferences can contain the parameter.
     * @param defaultValue returned if preference is not found.
     * @return the value connected with the preference's key present in the passed preferences or passed default value
     *         if not found. */
    public float getFloatOrElse(Preferences preferences, float defaultValue);

    /** @param preferences cannot be null.
     * @return true if the preference is present in the passed preferences. */
    public boolean isPresentIn(Preferences preferences);
}
