package com.github.czyzby.lml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Should annotate a method of a view class filled by the LML parser. LmlBefore-annotated methods are invoked before
 * the LML templates are parsed. They should have no arguments or be single-argument methods consuming
 * {@link com.github.czyzby.lml.parser.LmlParser} instance.
 *
 * @author MJ */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LmlBefore {
}
