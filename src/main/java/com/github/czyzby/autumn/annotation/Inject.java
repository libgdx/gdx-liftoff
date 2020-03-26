package com.github.czyzby.autumn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Allows to inject context dependencies to components.
 *
 * @author MJ */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
    /** @return the exact class type of injected component. If void class, field's type will be used instead. This value
     *         allows to resolve conflicts when multiple components share the same superclass that you want to inject -
     *         you can keep superclass in field's type, while still injecting an instance of a specific class. Also,
     *         interfaces are not supported on GWT, so if you want to inject a component to interface field type, this
     *         might be your only option. */
    Class<?>value() default void.class;

    /** @return if not void class, object of this class will be wrapped with
     *         {@link com.github.czyzby.kiwi.util.gdx.asset.lazy.Lazy} container and injected. Rather than creating the
     *         object at once, it will be initiated on first
     *         {@link com.github.czyzby.kiwi.util.gdx.asset.lazy.Lazy#get()} call. Note that using this setting makes
     *         sense only if {@link #newInstance()} returns true, as context component are all initiated at once anyway.
     *         This might be used for heavy objects (RSA keys, resources) with custom providers. */
    Class<?>lazy() default void.class;

    /** @return if true and lazy class is chosen, the constructed
     *         {@link com.github.czyzby.kiwi.util.gdx.asset.lazy.Lazy} object will be concurrent. */
    boolean concurrentLazy() default false;

    /** @return if true, a new instance of the variable's class will be injected rather than the one stored in context
     *         (if any is actually present). This instance will be unique to the object that has it injected and will
     *         not be available in context or managed by it - it will be created with no-arg constructor or by a
     *         provider of the selected type. */
    boolean newInstance() default false;
}
