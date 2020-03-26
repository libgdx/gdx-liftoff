package com.github.czyzby.autumn.processor;

import java.lang.annotation.Annotation;

import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.context.error.ContextInitiationException;

/** Utility abstract implementation of {@link AnnotationProcessor}. Reports that it doesn't support any type of
 * annotation; throws exceptions for all processing methods. Allows to create annotation processors for a single type
 * (for example: field processors) without boilerplate methods.
 *
 * @author MJ
 *
 * @param <SupportedAnnotation> type of supported annotation. */
public abstract class AbstractAnnotationProcessor<SupportedAnnotation extends Annotation>
        implements AnnotationProcessor<SupportedAnnotation> {
    @Override
    public boolean isSupportingFields() {
        return false;
    }

    @Override
    public boolean isSupportingMethods() {
        return false;
    }

    @Override
    public boolean isSupportingTypes() {
        return false;
    }

    @Override
    public void processField(final Field field, final SupportedAnnotation annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        throw new ContextInitiationException(this + " does not support field annotations processing.");
    }

    @Override
    public void processMethod(final Method method, final SupportedAnnotation annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        throw new ContextInitiationException(this + " does not support method annotations processing.");
    }

    @Override
    public void processType(final Class<?> type, final SupportedAnnotation annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        throw new ContextInitiationException(this + " does not support class annotations processing.");
    }

    @Override
    public void doBeforeScanning(final ContextInitializer initializer) {
    }

    @Override
    public void doAfterScanning(final ContextInitializer initializer, final Context context,
            final ContextDestroyer destroyer) {
    }
}
