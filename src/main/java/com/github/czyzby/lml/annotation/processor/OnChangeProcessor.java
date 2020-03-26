package com.github.czyzby.lml.annotation.processor;

import com.badlogic.gdx.utils.reflect.Field;

/** Common interface for handlers of {@link com.github.czyzby.lml.annotation.OnChange}-annotated fields.
 *
 * @author MJ */
public interface OnChangeProcessor {
    /** @param field {@link com.github.czyzby.lml.annotation.OnChange}-annotated field.
     * @param actor actor with the referenced ID present as the annotation argument.
     * @return true if this processor is able to handle this field and actor combination. */
    boolean canProcess(Field field, Object actor);

    /** @param field its value will be linked with the actor.
     * @param owner owner of the field.
     * @param actor will have a listener attached or will otherwise link one of its values with the field. */
    void process(Field field, Object owner, Object actor);
}
