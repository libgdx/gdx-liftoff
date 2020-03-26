package com.github.czyzby.autumn.mvc.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Allows to register a dialog globally.
 *
 * @author MJ */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewDialog {
    /** @return internal path to a LML template file containing a single dialog widget. */
    String value();

    /** @return ID of the dialog. Allows to specify dialog's name as it appears in the views with the default
     *         show:dialogId action. If the class implements {@link com.github.czyzby.lml.parser.action.ActionContainer}
     *         interface, it will be added as action container to the LML parser with the selected ID. If not set, will
     *         be replaced with the simple name of class object. */
    String id() default "";

    /** @return if true, LML template will be parsed once and the created dialog object will be shown each time it is
     *         referenced. Otherwise it is parsed on each call - allowing to specify dialog attributes, for example.
     *         Defaults to false. */
    boolean cacheInstance() default false;
}
