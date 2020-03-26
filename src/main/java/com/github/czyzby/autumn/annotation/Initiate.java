package com.github.czyzby.autumn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks the annotated method to be invoked after context is fully built. Allows to sort methods by priority, honoring
 * it among all components. Annotated methods' parameters will be provided by the context (either existing components or
 * instances provided by dependency providers. Methods with higher priority execute first.
 *
 * @author MJ
 * @see Provider */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Initiate {
    /** @return priority of the defined method. Priority is honored among all components, and not only class scoped -
     *         when multiple classes have initiation methods, their invocations will be globally sorted by priorities.
     *         Methods with higher priority execute first. */
    int priority() default 0;
}
