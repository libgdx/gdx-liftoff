package com.github.czyzby.lml.parser.impl.annotation.processor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;
import com.github.czyzby.lml.annotation.processor.OnChangeProcessor;

/** Abstract base for on change field processors. Attaches a custom {@link ChangeListener} to an actor in order to
 * update the field.
 *
 * @author MJ */
public abstract class AbstractOnChangeProcessor<Widget> implements OnChangeProcessor {
    @Override
    public void process(final Field field, final Object owner, final Object actor) {
        ((Actor) actor).addListener(new OnChangeListener<Widget>(this, owner, field));
    }

    /** @param actor has a change listener attached. Performs unchecked cast of the actor for extra utility, so
     *            {@link OnChangeProcessor#canProcess(Field, Object)} should be properly implemented to avoid class cast
     *            exceptions.
     * @return value extracted from the actor. Will be set as the annotated field's value. */
    protected abstract Object extractValueFromActor(Widget actor);

    private static class OnChangeListener<Widget> extends ChangeListener {
        private final AbstractOnChangeProcessor<Widget> processor;
        private final Object owner;
        private final Field field;

        public OnChangeListener(final AbstractOnChangeProcessor<Widget> processor, final Object owner,
                final Field field) {
            this.processor = processor;
            this.owner = owner;
            this.field = field;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void changed(final ChangeEvent event, final Actor actor) {
            try {
                Reflection.setFieldValue(field, owner, processor.extractValueFromActor((Widget) actor));
            } catch (final ReflectionException exception) {
                throw new GdxRuntimeException(
                        "Unable to update OnChange-annotated field: " + field + " of object: " + owner, exception);
            }
        }
    }
}
