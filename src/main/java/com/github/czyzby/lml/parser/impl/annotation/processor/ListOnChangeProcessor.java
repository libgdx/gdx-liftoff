package com.github.czyzby.lml.parser.impl.annotation.processor;

import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.utils.ArraySelection;
import com.badlogic.gdx.utils.reflect.Field;

/** Updates an ArraySelection value to the current selection using {@link List#getSelection()} result as string.
 *
 * @author MJ */
public class ListOnChangeProcessor extends AbstractOnChangeProcessor<List<?>> {
    @Override
    public boolean canProcess(final Field field, final Object actor) {
        return actor instanceof List<?> && field.getType().equals(ArraySelection.class);
    }

    @Override
    protected Object extractValueFromActor(final List<?> actor) {
        return actor.getSelection();
    }
}
