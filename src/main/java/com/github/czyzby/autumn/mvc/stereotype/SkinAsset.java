package com.github.czyzby.autumn.mvc.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks an object mapped by application's managed {@link com.badlogic.gdx.scenes.scene2d.ui.Skin}. Injected after skin
 * is fully loaded (on application init). Annotated field must have the same type as the mapped object.
 *
 * @author MJ */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SkinAsset {
    /** @return name of the object as it appears in the skin. Defaults to "default". */
    String value() default "default";

    /** @return name of the skin to extract asset from. Defaults to "default". */
    String skin() default "default";
}