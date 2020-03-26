package com.github.czyzby.autumn.processor.impl;

import com.github.czyzby.autumn.annotation.Processor;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.context.error.ContextInitiationException;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.autumn.processor.AnnotationProcessor;

/** Handles other processors. Registers annotation processors to context initializer.
 *
 * @author MJ */
public class MetaAnnotationProcessor extends AbstractAnnotationProcessor<Processor> {
    @Override
    public Class<Processor> getSupportedAnnotationType() {
        return Processor.class;
    }

    @Override
    public boolean isSupportingTypes() {
        return true;
    }

    @Override
    public void processType(final Class<?> type, final Processor annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        if (component instanceof AnnotationProcessor<?>) {
            initializer.addProcessor((AnnotationProcessor<?>) component);
        } else {
            throw new ContextInitiationException(
                    "@Processor annotation should annotate only classes that implement AnnotationProcessor interface.");
        }
    }
}
