package com.github.czyzby.lml.parser.impl.annotation.processor;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.ArraySelection;
import com.badlogic.gdx.utils.reflect.Field;

/** Updates an ArraySelection value to the current selection using {@link SelectBox#getSelection()} result as string.
 *
 * @author MJ */
public class SelectBoxOnChangeProcessor extends AbstractOnChangeProcessor<SelectBox<?>> {
    @Override
    public boolean canProcess(final Field field, final Object actor) {
        return actor instanceof SelectBox<?> && field.getType().equals(ArraySelection.class);
    }

    @Override
    protected Object extractValueFromActor(final SelectBox<?> actor) {
        return actor.getSelection();
    }
}
