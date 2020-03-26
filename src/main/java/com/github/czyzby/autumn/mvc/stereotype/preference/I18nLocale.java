package com.github.czyzby.autumn.mvc.stereotype.preference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Should annotate either string with a preference name and point to specific preferences with locale through
 * {@link I18nLocale#propertiesPath()}, string with a name of the locale or an actual Locale object.
 *
 * @author MJ */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nLocale {
    /** @return path to the properties containing locale. Optional if the variable is not a string and is an actual
     *         {@link java.util.Locale} instance. */
    String propertiesPath() default "";

    /** @return default locale that will be used if the field is a string and preferences path is given, but there is no
     *         locale preference available. Defaults to English. Separator defaults to "-".
     * @see com.github.czyzby.autumn.mvc.component.i18n.LocaleService#DEFAULT_LOCALE_SEPARATOR */
    String defaultLocale() default "en";
}
