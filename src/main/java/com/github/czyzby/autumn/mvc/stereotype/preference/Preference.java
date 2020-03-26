package com.github.czyzby.autumn.mvc.stereotype.preference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Should annotate a string field with a path to a preference name. Proposed to be kept in a single configuration
 * component.
 *
 * @author MJ
 * @see com.badlogic.gdx.Preferences */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Preference {
    /** @return name of the preferences as it appears in LML templates. Optional if one preferences are used. Defaults
     *         to "default". */
    String value() default "default";
}
