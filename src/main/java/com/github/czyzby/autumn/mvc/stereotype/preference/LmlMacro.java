package com.github.czyzby.autumn.mvc.stereotype.preference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.badlogic.gdx.Files.FileType;

/** Since LML macros have to be parsed once - specific macro parsers will be created and registered dynamically - this
 * routine can be done by using this annotation on a string or array of strings containing path(s) to .lml file(s)
 * containing the macros.
 *
 * @author MJ */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LmlMacro {
    /** @return file type of the stored macro. */
    FileType fileType() default FileType.Internal;
}
