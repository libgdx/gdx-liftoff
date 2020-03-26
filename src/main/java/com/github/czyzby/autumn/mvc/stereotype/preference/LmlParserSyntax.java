package com.github.czyzby.autumn.mvc.stereotype.preference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.czyzby.lml.parser.LmlParser;

/** Should annotate a non-empty field with an object that implements {@link LmlParser}. Field's value will be set as the
 * currently used LML parser syntax.
 *
 * @author MJ */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LmlParserSyntax {
}
