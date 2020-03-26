package com.github.czyzby.autumn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Turns the method into a listener for events of the selected class. Listener can be optionally removed - if the
 * method returns a boolean and it value matches false, listener will be removed.
 *
 * <p>
 * Can annotate a class that implements {@link com.github.czyzby.autumn.processor.event.EventListener}. Instead of using
 * reflection, such class will be able to process events directly with its method. This is preferred for commonly used
 * actions.
 *
 * @author MJ */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OnEvent {
    /** Utility values for code clarity. If the annotated methods or implemented listeners return boolean value, it can
     * control whether the listener is removed. */
    boolean KEEP = true, REMOVE = false;

    /** @return the class of event that is listened to by this method. When an event of this type occurs, the method is
     *         fired and - optionally - if any of the parameters shares the type with the event, it will be passed to
     *         the method. */
    Class<?>value();

    /** @return if set to true, event listener created with this method will be removed from dispatcher after first
     *         invocation. Note that when set to true, it ignores the result of the method - even if it returns
     *         booleans. Defaults to false. */
    boolean removeAfterInvocation() default false;

    /** @return if true, method invocations will be scheduled on the main thread using Gdx.app.postRunnable(Runnable).
     *         Otherwise, the thread that posts the event will invoke the listener. Defaults to false. */
    boolean forceMainThread() default false;

    /** @return if true, throws exception if unable to execute event. Otherwise, exceptions are ignored. Setting
     *         available only for methods - {@link com.github.czyzby.autumn.processor.event.EventListener}
     *         implementation should handle their own exceptions. */
    boolean strict() default true;
}
