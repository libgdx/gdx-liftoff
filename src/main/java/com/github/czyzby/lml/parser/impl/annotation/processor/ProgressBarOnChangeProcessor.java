package com.github.czyzby.lml.parser.impl.annotation.processor;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.reflect.Field;

/** Attaches listener to a {@code ProgressBar} that will modify float or Float field according to current value of a
 * progress bar or a slider. Field value matches {@link ProgressBar#getValue()} result.
 *
 * @author MJ */
public class ProgressBarOnChangeProcessor extends AbstractOnChangeProcessor<ProgressBar> {
    @Override
    public boolean canProcess(final Field field, final Object actor) {
        return actor instanceof ProgressBar
                && (field.getType().equals(float.class) || field.getType().equals(Float.class));
    }

    @Override
    protected Object extractValueFromActor(final ProgressBar actor) {
        return actor.getValue();
    }
}
