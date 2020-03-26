package com.github.czyzby.autumn.mvc.component.preferences.dto;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** Wraps around a single preference, allowing to manage it.
 *
 * @author MJ
 *
 * @param <Type> type of stored preference.
 * @see com.github.czyzby.autumn.mvc.stereotype.preference.Property */
public interface Preference<Type> {
    /** @param name name of this preference.
     * @param preferences contain a value mapped to the chosen preference's key, although it is not validated.
     * @throws Exception if unable to read preferences. If an exception is thrown, {@link #getDefault()} will be used as
     *             preference value. */
    void read(String name, Preferences preferences) throws Exception;

    /** @return default preference value. Used if preference is absent in the preferences file. */
    Type getDefault();

    /** @param actor used to set up the preference. Has preference setting action attached.
     * @return preference value extracted from the actor. */
    Type extractFromActor(Actor actor);

    /** @return current preference value. */
    Type get();

    /** @param preference will become current preference value. */
    void set(Type preference);

    /** @param name name of this preference.
     * @param preferences should store the preference. */
    void save(String name, Preferences preferences);

    /** Returns current preference value.
     *
     * @author MJ
     *
     * @param <Type> type of the preference. */
    public static class PreferenceGetter<Type> implements ActorConsumer<Type, Object> {
        private final Preference<Type> preference;

        public PreferenceGetter(final Preference<Type> preference) {
            this.preference = preference;
        }

        @Override
        public Type consume(final Object actor) {
            return preference.get();
        }
    }

    /** Sets and returns current preference value.
     *
     * @author MJ
     *
     * @param <Type> type of the preference. */
    public static class PreferenceSetter<Type> implements ActorConsumer<Type, Actor> {
        private final Preference<Type> preference;

        public PreferenceSetter(final Preference<Type> preference) {
            this.preference = preference;
        }

        @Override
        public Type consume(final Actor actor) {
            preference.set(preference.extractFromActor(actor));
            return preference.get();
        }
    }
}
