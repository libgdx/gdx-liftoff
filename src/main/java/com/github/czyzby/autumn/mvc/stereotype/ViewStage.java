package com.github.czyzby.autumn.mvc.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Allows to inject a {@link com.badlogic.gdx.scenes.scene2d.Stage} managed by the wrapping controller object. Note
 * that stage might be injected (or even cleared) multiple times, as views are reloaded on occasions (locale change, on
 * demand) and dialogs can be shown any number of times. Declared field has to be inside a class annotated with
 * {@link View} or {@link ViewDialog}.
 * Stages are NOT initiated upon controllers creation - they are usually constructed after the first transition to the
 * specified view - so be careful with object initiations.
 *
 * @author MJ */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewStage {
}
