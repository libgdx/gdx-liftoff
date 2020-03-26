package com.github.czyzby.autumn.mvc.component.preferences.dto;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

/** Abstract base common for JSON-serialized preferences. Will automatically encode the JSON strings with BASE64 to
 * avoid forbidden characters on all platforms.
 *
 * @author MJ
 *
 * @param <Type> supported preference type. Has to be possible to serialize with {@link Json}.
 * @see #createJson() */
public abstract class AbstractJsonPreference<Type> implements Preference<Type> {
    private Type preference;
    private final Json json = createJson();

    /** @return {@link Json} instance used to serialize and deserialize the preference. Reuse a single instance for all
     *         JSON preferences to optimize this. */
    protected Json createJson() {
        return new Json();
    }

    /** @return direct reference to JSON encoder. */
    public Json getJson() {
        return json;
    }

    /** @return type of stored preference wrapper. */
    protected abstract Class<Type> getType();

    @Override
    public void read(final String name, final Preferences preferences) throws Exception {
        final String encodedPreference = preferences.getString(name);
        preference = json.fromJson(getType(), Base64Coder.decodeString(encodedPreference));
    }

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
        preferences.putString(name, Base64Coder.encodeString(json.toJson(preference, getType())));
    }
}
