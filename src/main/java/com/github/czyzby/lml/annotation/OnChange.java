package com.github.czyzby.lml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Attaches a listener to the actor with the specified ID. If the actor's state changes, OnChange-annotated field value
 * will be updated. This does not work with all widgets, unless you specify custom handlers. Default handlers are
 * provided for:
 *
 * <ul>
 * <li>buttons (text buttons, check boxes, image buttons, image text buttons): supports boolean and Boolean fields;
 * matches checked status.
 * <li>progress bar (and slider): supports float and Float; matches current progress.
 * <li>text field (and text area): supports String; matches current text.
 * <li>list (and select box, which contains a list): supports String and ArraySelection; matches current selection.
 * </ul>
 *
 * <p>
 * Note that this does not work the other way around: if you set field's value manually, it will not modify widget's
 * state. So, if you need to be able to directly access the actor, consider using "onChange" tag attribute and attaching
 * an action to the widget - the method referenced by this attribute can receive the actor as its argument.
 *
 * @author MJ
 * @see com.github.czyzby.lml.annotation.processor.OnChangeProcessor */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface OnChange {
    /** @return ID of the handled actor, as specified with "id" attribute in LML template. */
    String value();
}
