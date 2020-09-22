package com.github.czyzby.kiwi.util.gdx.preference;

import com.badlogic.gdx.Preferences;

/** "Abstract" implementation for the {@link Preference} interface, although the class itself is not abstract and can be
 * normally used in your code. If you want to extend it though, you should know that all setter and get methods use
 * {@link #getName()} method to put and get preference values from the preferences.
 *
 * Preferences are advised to be kept in an enum (which cannot extend), so this implementation might have to be copied.
 *
 * @author MJ */
public class PreferenceWrapper implements Preference {
    private final String preferenceName;

    public PreferenceWrapper(final String preferenceName) {
        this.preferenceName = preferenceName;
    }

    @Override
    public String getName() {
        return preferenceName;
    }

    @Override
    public void setIn(final Preferences preferences, final String preferenceValue) {
        preferences.putString(getName(), preferenceValue);
    }

    @Override
    public void setIn(final Preferences preferences, final boolean preferenceValue) {
        preferences.putBoolean(getName(), preferenceValue);
    }

    @Override
    public void setIn(final Preferences preferences, final int preferenceValue) {
        preferences.putInteger(getName(), preferenceValue);
    }

    @Override
    public void setIn(final Preferences preferences, final long preferenceValue) {
        preferences.putLong(getName(), preferenceValue);
    }

    @Override
    public void setIn(final Preferences preferences, final float preferenceValue) {
        preferences.putFloat(getName(), preferenceValue);
    }

    @Override
    public String getStringFrom(final Preferences preferences) {
        return preferences.getString(getName());
    }

    @Override
    public String getStringOrElse(final Preferences preferences, final String defaultValue) {
        return preferences.getString(getName(), defaultValue);
    }

    @Override
    public boolean getBooleanFrom(final Preferences preferences) {
        return preferences.getBoolean(getName());
    }

    @Override
    public boolean getBooleanOrElse(final Preferences preferences, final boolean defaultValue) {
        return preferences.getBoolean(getName(), defaultValue);
    }

    @Override
    public int getIntFrom(final Preferences preferences) {
        return preferences.getInteger(getName());
    }

    @Override
    public int getIntOrElse(final Preferences preferences, final int defaultValue) {
        return preferences.getInteger(getName(), defaultValue);
    }

    @Override
    public long getLongFrom(final Preferences preferences) {
        return preferences.getLong(getName());
    }

    @Override
    public long getLongOrElse(final Preferences preferences, final long defaultValue) {
        return preferences.getLong(getName(), defaultValue);
    }

    @Override
    public float getFloatFrom(final Preferences preferences) {
        return preferences.getFloat(getName());
    }

    @Override
    public float getFloatOrElse(final Preferences preferences, final float defaultValue) {
        return preferences.getFloat(getName(), defaultValue);
    }

    @Override
    public boolean isPresentIn(final Preferences preferences) {
        return preferences.contains(getName());
    }
}
