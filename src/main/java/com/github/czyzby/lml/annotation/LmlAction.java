package com.github.czyzby.lml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Allows to select an {@link com.github.czyzby.lml.parser.action.ActionContainer}'s action with a custom ID instead of
 * its method or field name. This allows containers to be refactored without breaking the templates. Also, this has
 * another significant advantage: action containers are scanned and processed upon registration and its annotated
 * methods and fields will be mapped immediately, making action look-up very cheap. Other, unannotated methods will have
 * to be found by reflection look-up based on parameter superclass tree, which might be much slower and error-prone.
 * Annotate methods and fields expected to be used in LML templates, unless you're prototyping, not going to refactor or
 * obfuscate your code, or simply want to write less boilerplate code by reducing the amount of annotations used.
 *
 * @author MJ */
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LmlAction {
    /** @return IDs of the method. Each and every of them can be used to reference this method in a LML template.
     *         Default parser implementation will ignore case of the IDs, so even if a method is mapped to "action" key
     *         in the annotation, it can still be referenced as "Action", "ACTION", "acTIon" (and so on) in the
     *         templates. If no IDs are given, method name will be used as ID instead. */
    String[] value() default {};
}
