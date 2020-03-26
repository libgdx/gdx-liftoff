package com.github.czyzby.lml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** When a view is filled by a LML parser, its fields (and their annotations) are processed. However, complex views
 * might contain many actors with unique IDs that should be injected and view classes might get messy really fast. This
 * annotation allows to "delegate" some of the injection to a container class. For example:
 *
 * <blockquote>
 *
 * <pre>
 * public class SomeView implements LmlView {
 *    {@literal @}LmlActor("someId") Button button;
 *    {@literal @}LmlInject MyContainer container;
 *    (...)
 * }
 *
 * public class MyContainer {
 *    {@literal @}LmlActor("someOtherId") Button button;
 *    {@literal @}LmlActor("labelId") Label label;
 *    {@literal @}OnChange("loading") float progress;
 *    // Getters, etc.
 *    (...)
 * }
 * </pre>
 *
 * </blockquote> Upon filling {@code SomeView} instance after LML template parsing, an instance of {@code MyContainer}
 * will be created with public no-arg constructor, its fields will be filled and it will be injected in to
 * {@code SomeView "container"} field.
 *
 * <p>
 * This annotation basically allows to process fields of foreign classes that are referenced in view's fields. New
 * instances of objects are created only when current field value is null or {@link #newInstance()} returns true;
 * otherwise current field value will be processed and filled.
 *
 * <p>
 * Note that while injected containers can have {@link LmlInject}-annotated fields, circular references are not
 * detected. If, for example, {@code SomeView} has {@link LmlInject}-annotated field of {@code SomeView} type, injection
 * would never finish correctly and an exception would be eventually thrown.
 *
 * <p>
 * This annotation can also annotate a field with {@link com.github.czyzby.lml.parser.LmlParser} type (or with LmlParser
 * class in {@link #value()}, which will have the same effect, but will let you use a more specific parser type). Parser
 * used to fill the view will be injected. For example:
 *
 * <blockquote>
 *
 * <pre>
 * {@literal @}LmlInject LmlParser parser;
 * {@literal @}LmlInject(LmlParser.class) DefaultLmlParser castedParser;
 * </pre>
 *
 * </blockquote>
 *
 * @author MJ */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LmlInject {
    /** @return exact class of the object that should be injected. This is an optional setting that allows to keep a
     *         different field type than the actual injected object class. For example, you might want to keep field's
     *         type as an interface and inject the actual implementation. */
    Class<?>value() default void.class;

    /** Defaults to false.
     *
     * @return if true, a new instance will be created, filled and injected each time the view is filled. If false and
     *         current field value is not null, current field value will be processed and filled. */
    boolean newInstance() default false;
}
