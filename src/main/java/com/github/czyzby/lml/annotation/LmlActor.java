package com.github.czyzby.lml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Utility annotation for actor injection. If you want a particular object to contain some widgets created with LML,
 * you can either manually set its fields - the usual way - or mark them with this annotation and pass its instance to
 * the parser to let it inject its actor dependencies.
 *
 * <p>
 * Parsers handle both single actor injection or merging multiple actors with libGDX collections and injecting them into
 * one field. {@link com.badlogic.gdx.utils.Array}, {@link com.badlogic.gdx.utils.ObjectSet} and
 * {@link com.badlogic.gdx.utils.ObjectMap} (of strings) are supported. If an Array is used, order of actors is
 * preserved and matches the order of given IDs. ObjectSet provides no such guarantees, but it should be used if you
 * want to remove duplicates. ObjectMaps are expected to use strings as keys; injected actors will be mapped with their
 * IDs.
 *
 * <p>
 * Example usage - LML template: <blockquote>
 *
 * <pre>
 * &lt;textButton id=button/&gt;
 * &lt;label id=one/&gt;&lt;label id=two/&gt;
 * </pre>
 *
 * </blockquote>
 *
 * <p>
 * View class: <blockquote>
 *
 * <pre>
 * &#64;LmlActor("button") TextButton button;
 * &#64;LmlActor({ "one", "two" }) Array&lt;Label&gt; labels;
 * &#64;LmlActor({ "one", "two", "two" }) ObjectSet&lt;Label&gt; uniqueLabels;
 * &#64;LmlActor({ "one", "two" }) ObjectMap&lt;String, Label&gt; mappedLabels;
 * </pre>
 *
 * </blockquote>
 *
 * Button will have the textButton with "id=button" attribute injected. "labels" array will be emptied and filled with
 * two labels (preserving the order: "one" will be first, "two" will be second). A new set will be created for
 * uniqueLabels fields and it will contain only 2 labels (even though 3 arguments are passed to the annotation), as
 * duplicates will be removed. "mappedLabels" will contain the first label mapped to "one" string and second to "two".
 *
 * <p>
 * LML arrays are supported in this annotation. For example, these two fields would have the same values injected:
 * <blockquote>
 *
 * <pre>
 * &#64;LmlActor("element;range[0,2]") Array&lt;Label&gt; labels;
 * &#64;LmlActor({ "element", "range0", "range1", "range2" }) Array&lt;Label&gt; labels;
 * </pre>
 *
 * </blockquote>This is especially useful if you want to inject a collection of actors created with some argument - LML
 * arguments ({argumentName}) are supported as well.
 *
 * @author MJ */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LmlActor {
    /** @return ID(s) of the actor(s) in the LML template, referenced by the "id" tag attribute. If no value is chosen,
     *         field name will be used instead. */
    String[] value() default {};
}
