package com.github.czyzby.lml.parser.impl.annotation.processor;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.reflect.Field;

/** Attaches a listener to a {@link TextField}, changing a String field according to its value. Field's value will match
 * {@link TextField#getText()} result.
 *
 * @author MJ */
public class TextFieldOnChangeProcessor extends AbstractOnChangeProcessor<TextField> {
    @Override
    public boolean canProcess(final Field field, final Object actor) {
        return actor instanceof TextField && field.getType().equals(String.class);
    }

    @Override
    protected Object extractValueFromActor(final TextField actor) {
        return actor.getText();
    }
}
