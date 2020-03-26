package com.github.czyzby.lml.parser.impl.annotation.processor;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.reflect.Field;

/** Attaches {@link ChangeListener} to a {@link Button}, setting boolean or Boolean field on change event. The field
 * value matches {@link Button#isChecked()} result.
 *
 * @author MJ */
public class ButtonOnChangeProcessor extends AbstractOnChangeProcessor<Button> {
    @Override
    public boolean canProcess(final Field field, final Object actor) {
        return actor instanceof Button
                && (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class));
    }

    @Override
    protected Object extractValueFromActor(final Button actor) {
        return actor.isChecked();
    }
}
