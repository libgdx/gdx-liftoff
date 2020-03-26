package com.github.czyzby.autumn.mvc.stereotype.preference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.badlogic.gdx.Files.FileType;

/** Should annotate a string field with a path to a bundle. Proposed to be kept in a single configuration component.
 *
 * @author MJ */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nBundle {
    /** @return key of the bundle as it appears in LML templates. */
    String value() default "default";

    /** @return type of the bundle file. */
    FileType fileType() default FileType.Internal;
}
