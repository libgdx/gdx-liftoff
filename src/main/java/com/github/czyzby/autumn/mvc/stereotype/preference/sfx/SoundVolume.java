package com.github.czyzby.autumn.mvc.stereotype.preference.sfx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Should annotate either name of the preference containing sound volume or a float variable with the initial sound
 * volume.
 *
 * @author MJ */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SoundVolume {
    /** @return path to the preferences. Optional if variable is not a string with preference name. Should match other
     *         music preferences. */
    String preferences() default "";

    /** @return default volume, used if not found in preferences. */
    float defaultVolume() default 1f;
}