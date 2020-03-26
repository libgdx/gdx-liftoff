package com.github.czyzby.autumn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** If annotates a class and it's a component, it will be eventually disposed of by
 * {@link com.github.czyzby.autumn.context.ContextDestroyer}. If annotates a field and its value is not null, it will be
 * disposed of upon context destruction.
 *
 * <p>
 * Note that by annotating a field or component, you automatically keep a reference to component in
 * {@link com.github.czyzby.autumn.context.ContextDestroyer}. If you want the component garbage-collected just after the
 * initiation, make sure to do it manually.
 *
 * @author MJ */
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Dispose {
}
