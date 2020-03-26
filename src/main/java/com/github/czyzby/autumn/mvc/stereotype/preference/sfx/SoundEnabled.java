package com.github.czyzby.autumn.mvc.stereotype.preference.sfx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Should annotate either name of the preference containing sound setting or a boolean variable with the initial value.
 *
 * @author MJ */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SoundEnabled {
    /** @return path to the preferences. Optional if variable is not a string with preference name. Should match other
     *         music preferences. */
    String preferences() default "";

    /** @return default setting, used if not found in preferences. */
    boolean defaultSetting() default true;
}