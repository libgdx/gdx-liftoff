package com.github.czyzby.autumn.mvc.stereotype.preference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Should annotate a string field with a path to a skin. Extension (.json) is optional. For example, if you have a
 * "ui.json" file located at "interface" folder (in assets), you can pass "interface/ui". Annotated value is proposed to
 * be kept in a single configuration component.
 *
 * @author MJ */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Skin {
    /** @return name of the skin, as it will be available in LML templates. Defaults to "default". */
    String value() default "default";

    /** @return paths to fonts. Allows to load fonts with the same atlas as the skin. Note that fonts names should be
     *         passed with {@link #fontNames()}. */
    String[]fonts() default {};

    /** @return names of the fonts as they appear in the skin. Allows to use font loaded from skin's atlas, without
     *         having to reference them in JSON files. Names array length has to match {@link #fonts()} length. */
    String[]fontNames() default {};
}
