package com.github.czyzby.lml.parser.impl.action;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Field;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** Wraps around a reflected field, implementing actor consumer interface. Returns current field value on action
 * invocation.
 *
 * @author MJ */
public class FieldActorConsumer implements ActorConsumer<Object, Object> {
    private final Field field;
    private final Object fieldOwner;

    public FieldActorConsumer(final Field field, final Object fieldOwner) {
        if (field == null || fieldOwner == null) {
            throw new IllegalArgumentException(
                    "Field actor consumer has to wrap around an existing field and its owner.");
        }
        this.field = field;
        this.fieldOwner = fieldOwner;
    }

    @Override
    public Object consume(final Object actor) {
        try {
            return Reflection.getFieldValue(field, fieldOwner);
        } catch (final Exception exception) {
            throw new GdxRuntimeException(
                    "Unable to extract field value from field: " + field + " of object: " + fieldOwner, exception);
        }
    }
}
