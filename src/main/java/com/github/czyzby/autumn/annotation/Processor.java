package com.github.czyzby.autumn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Should annotate {@link com.github.czyzby.autumn.processor.AnnotationProcessor} implementations. These classes are
 * scanned for and initiated before other components, so they can process other components' annotated fields, methods
 * and types.
 *
 * <p>
 * Note that processors are meta-components and are initiated BEFORE regular components, as they are used to process
 * other components' annotations. While processors can have their fields injected, methods processed (etc), they should
 * reference only other meta-components to be properly initiated.
 *
 * @author MJ
 * @see com.github.czyzby.autumn.processor.AbstractAnnotationProcessor */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Processor {
}
