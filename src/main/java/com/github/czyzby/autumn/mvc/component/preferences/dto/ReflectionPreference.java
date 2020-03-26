package com.github.czyzby.autumn.mvc.component.preferences.dto;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Reflected field wrapper. Treats a field as a preference.
 *
 * @author MJ */
public class ReflectionPreference implements Preference<Object> {
    private final Object owner;
    private final Field field;
    private final Type type;

    public ReflectionPreference(final Object owner, final Field field) throws GdxRuntimeException {
        this.owner = owner;
        this.field = field;
        type = Type.getType(field);
        if (type == null) {
            throw new GdxRuntimeException("Invalid field annotated with @Property: unable to handle: " + field.getType()
                    + " in field: " + field + " of component: " + owner);
        }
    }

    @Override
    public void read(final String name, final Preferences preferences) throws Exception {
        Reflection.setFieldValue(field, owner, type.convert(preferences.getString(name)));
    }

    @Override
    public Object getDefault() {
        return type.getDefault();
    }

    @Override
    public Object extractFromActor(final Actor actor) {
        return type.extractFromActor(actor);
    }

    @Override
    public Object get() {
        try {
            return Reflection.getFieldValue(field, owner);
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException("Unable to extract field value from component: " + owner, exception);
        }
    }

    @Override
    public void set(final Object preference) {
        try {
            Reflection.setFieldValue(field, owner, preference);
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException("Unable to set field value in component: " + owner, exception);
        }
    }

    @Override
    public void save(final String name, final Preferences preferences) {
        preferences.putString(name, String.valueOf(get()));
    }

    /** Contains all supported types.
     *
     * @author MJ */
    private static enum Type {
        BOOLEAN(boolean.class, Boolean.class) {
            @Override
            public Object convert(final String raw) {
                return Boolean.valueOf(raw);
            }

            @Override
            public Object getDefault() {
                return Boolean.TRUE;
            }

            @Override
            public Object extractFromActor(final Actor actor) {
                if (actor instanceof Button) {
                    return ((Button) actor).isChecked();
                }
                throw new GdxRuntimeException("Cannot use default setter of boolean preference with actor: " + actor);
            }
        },
        INT(int.class, Integer.class) {
            @Override
            public Object convert(final String raw) {
                return Integer.valueOf(raw);
            }

            @Override
            public Object getDefault() {
                return Integer.valueOf(0);
            }

            @Override
            public Object extractFromActor(final Actor actor) {
                if (actor instanceof TextField) {
                    return Integer.valueOf(((TextField) actor).getText());
                } else if (actor instanceof List<?>) {
                    return ((List<?>) actor).getSelectedIndex();
                } else if (actor instanceof SelectBox<?>) {
                    return ((SelectBox<?>) actor).getSelectedIndex();
                }
                throw new GdxRuntimeException("Cannot use default setter of int preference with actor: " + actor);
            }
        },
        FLOAT(float.class, Float.class) {
            @Override
            public Object convert(final String raw) {
                return Float.valueOf(raw);
            }

            @Override
            public Object getDefault() {
                return Float.valueOf(0f);
            }

            @Override
            public Object extractFromActor(final Actor actor) {
                if (actor instanceof TextField) {
                    return Float.valueOf(((TextField) actor).getText());
                }
                throw new GdxRuntimeException("Cannot use default setter of float preference with actor: " + actor);
            }
        },
        LONG(long.class, Long.class) {
            @Override
            public Object convert(final String raw) {
                return Long.valueOf(raw);
            }

            @Override
            public Object getDefault() {
                return Long.valueOf(0L);
            }

            @Override
            public Object extractFromActor(final Actor actor) {
                if (actor instanceof TextField) {
                    return Long.valueOf(((TextField) actor).getText());
                }
                throw new GdxRuntimeException("Cannot use default setter of long preference with actor: " + actor);
            }
        },
        STRING(String.class, Object.class) {
            @Override
            public Object convert(final String raw) {
                return raw;
            }

            @Override
            public Object getDefault() {
                return Strings.EMPTY_STRING;
            }

            @Override
            public Object extractFromActor(final Actor actor) {
                if (actor instanceof Label) {
                    return ((Label) actor).getText().toString();
                } else if (actor instanceof TextButton) {
                    return ((TextButton) actor).getText();
                } else if (actor instanceof TextField) {
                    return ((TextField) actor).getText();
                } else if (actor instanceof List<?>) {
                    return Strings.toString(((List<?>) actor).getSelected(), Strings.EMPTY_STRING);
                } else if (actor instanceof SelectBox<?>) {
                    return Strings.toString(((SelectBox<?>) actor).getSelected(), Strings.EMPTY_STRING);
                }
                return Strings.toString(actor);
            }
        };

        private final Class<?>[] supportedClasses;

        private Type(final Class<?>... supportedClasses) {
            this.supportedClasses = supportedClasses;
        }

        /** @return default field value for the selected field type. */
        public abstract Object getDefault();

        /** @param field contains a preference.
         * @return type ready to handle field's value or null. */
        public static Type getType(final Field field) {
            for (final Type type : values()) {
                if (type.matches(field)) {
                    return type;
                }
            }
            return null;
        }

        /** @param field contains the preference.
         * @return true if this type can handle the field. */
        public boolean matches(final Field field) {
            for (final Class<?> supportedClass : supportedClasses) {
                if (supportedClass.equals(field.getType())) {
                    return true;
                }
            }
            return false;
        }

        /** @param raw preference value in the map.
         * @return converted preference value, ready to be stored in the field. */
        public abstract Object convert(String raw);

        /** @param actor used to set up the preference.
         * @return value extracted from the actor. */
        public abstract Object extractFromActor(final Actor actor);
    }
}
