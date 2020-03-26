package com.github.czyzby.autumn.mvc.stereotype.preference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Should annotate an array of strings that contain supported locales. Optional setting that allows to pass the
 * selected array as LML attribute "locales" (by default, can be changed with view argument name parameter), making it
 * easier to create locale changing buttons, for example. Additionally, it registers view methods with the selected
 * prefix that allow to change the current locale upon invocation (be careful though - this will reload views if the
 * locale does match the current one). For example, using default settings, annotated { "en-US", "pl" } array will
 * register "locale:en-US" and "locale:pl" locale changing actions and assign "en-US;pl" LML view array argument to
 * ${locales}.
 *
 * @author MJ */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AvailableLocales {
    /** @return prefix of the LML view method that allows to change current locale. Defaults to "locale:". */
    String localeChangeMethodPrefix() default "locale:";

    /** @return name of the locales array as it appears in the LML views. Defaults to "locales". */
    String viewArgumentName() default "locales";
}
