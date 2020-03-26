package com.github.czyzby.autumn.mvc.stereotype.preference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Describes a single application preference stored in selected {@link com.badlogic.gdx.Preferences}. Adds "set{name}"
 * and "get{name}" actions to LML, allowing to easily set up the application. Can annotate a field (storing the
 * preference value) or a specialized {@link com.github.czyzby.autumn.mvc.component.preferences.dto.Preference},
 * allowing to fully set up how the preference is handled. Upon context destruction, all preferences are saved.
 * <p>
 * Note that Property-annotated classes are fully initiated components: they can have injected fields (or be injected),
 * destruction methods, etc.
 *
 * @author MJ
 * @see com.badlogic.gdx.Preferences */
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
    /** @return name of the preference, as it should appear in the preferences file. Optional; if not given, class/field
     *         name is used. */
    String value() default "";

    /** @return name of the preferences which contain this property. Have to be set up with {@link Preference} first.
     *         Optional, defaults to "default".
     * @see Preference */
    String preferences() default "default";
}
