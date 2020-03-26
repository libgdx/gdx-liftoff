package com.github.czyzby.autumn.processor.impl;

import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;

/** Used to process {@link Component} annotation. Maps the component by a chosen interface.
 *
 * @author MJ */
public class ComponentAnnotationProcessor extends AbstractAnnotationProcessor<Component> {
    @Override
    public Class<Component> getSupportedAnnotationType() {
        return Component.class;
    }

    @Override
    public boolean isSupportingTypes() {
        return true;
    }

    @Override
    public void processType(final Class<?> type, final Component annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        for (final Class<?> interfaceType : annotation.value()) {
            context.add(interfaceType, component);
        }
    }
}
