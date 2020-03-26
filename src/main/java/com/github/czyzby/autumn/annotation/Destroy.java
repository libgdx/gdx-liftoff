package com.github.czyzby.autumn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks the annotated method to be invoked on {@link com.github.czyzby.autumn.context.ContextDestroyer#dispose()}
 * call. Allows to sort methods by priority, honoring it among all components. Annotated methods' parameters will be
 * provided by the context (either existing components or instances provided by dependency providers). However, keep in
 * mind that parameter instances will be kept in {@link com.github.czyzby.autumn.context.ContextDestroyer} object, so if
 * you want method dependencies garbage-collected before final context destruction, remove or replace the dependencies.
 * Methods with higher priority execute first.
 *
 * @author MJ
 * @see Provider */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Destroy {
    /** @return priority of the defined method. Priority is honored among all components, and not only class scoped -
     *         when multiple classes have initiation methods, their invocations will be globally sorted by priorities.
     *         Methods with higher priority execute first. */
    int priority() default 0;
}
