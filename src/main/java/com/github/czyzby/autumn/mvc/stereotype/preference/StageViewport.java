package com.github.czyzby.autumn.mvc.stereotype.preference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Should annotate an {@link com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider} (field or class
 * implementation) that provides {@link com.badlogic.gdx.utils.viewport.Viewport} objects for stages.
 *
 * @author MJ */
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface StageViewport {
}
