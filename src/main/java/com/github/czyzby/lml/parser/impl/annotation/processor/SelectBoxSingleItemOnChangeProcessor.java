package com.github.czyzby.lml.parser.impl.annotation.processor;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.utils.reflect.Field;
import com.github.czyzby.kiwi.util.common.Nullables;

/** Updates a String value to the current selection using {@link SelectBox#getSelected()} result as string.
 *
 * @author MJ */
public class SelectBoxSingleItemOnChangeProcessor extends AbstractOnChangeProcessor<SelectBox<?>> {
    @Override
    public boolean canProcess(final Field field, final Object actor) {
        return actor instanceof SelectBox<?> && field.getType().equals(String.class);
    }

    @Override
    protected Object extractValueFromActor(final SelectBox<?> actor) {
        return Nullables.toString(actor.getSelected(), null);
    }
}
