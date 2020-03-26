package com.github.czyzby.autumn.mvc.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Allows to register an {@link com.github.czyzby.lml.parser.action.ActionContainer} or
 * {@link com.github.czyzby.lml.parser.action.ActorConsumer} as view actions to multiple views at once. The annotated
 * class has to implement one of the mentioned interfaces, otherwise it will throw an exception.
 *
 * @author MJ */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewActionContainer {
    /** @return ID of the action container or action. */
    String value();

    /** @return list of IDs of views that the action or action container should be available to. Defaults to empty
     *         array, which adds the action to all views. */
    String[]views() default {};
}