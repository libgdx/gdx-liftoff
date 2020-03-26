package com.github.czyzby.autumn.mvc.component.preferences.dto;

import com.badlogic.gdx.Preferences;

/** Abstract base for a class implementing {@link Preference}.
 *
 * @author MJ
 *
 * @param <Type> type of the preference. */
public abstract class AbstractPreference<Type> implements Preference<Type> {
    private Type preference;

    @Override
    public void read(final String name, final Preferences preferences) throws Exception {
        preference = convert(preferences.getString(name));
    }

    /** @param rawPreference raw preference value stored in the preferences file.
     * @return preference converted to the chosen preference type.
     * @see #convert(String) */
    protected abstract Type convert(String rawPreference);

    /** @param preference current preference value.
     * @return value that should be stored in the preferences file as the raw preference value.
     * @see #convert(String) */
    protected abstract String serialize(Type preference);

    @Override
    public Type get() {
        return preference;
    }

    @Override
    public void set(final Type preference) {
        this.preference = preference;
    }

    @Override
    public void save(final String name, final Preferences preferences) {
        preferences.putString(name, serialize(preference));
    }
}
