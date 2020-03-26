package com.github.czyzby.autumn.processor.impl;

import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.context.error.ContextInitiationException;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.Lazy;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ReflectionObjectProvider;
import com.github.czyzby.kiwi.util.gdx.reflection.Annotations;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Processes {@link Inject}-annotated fields. Injects dependencies into components.
 *
 * @author MJ */
public class InjectAnnotationProcessor extends AbstractAnnotationProcessor<Inject> {
    @Override
    public Class<Inject> getSupportedAnnotationType() {
        return Inject.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void processField(final Field field, final Inject annotation, final Object component, final Context context,
            final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        if (Annotations.isNotVoid(annotation.lazy())) {
            processLazyInjection(field, annotation, component, context);
        } else {
            processRegularInjection(field, annotation, component, context);
        }
    }

    /** @param field will have its value injected.
     * @param annotation used to determine dependency type.
     * @param component owner of the field.
     * @param context used to resolve dependencies. */
    protected void processRegularInjection(final Field field, final Inject annotation, final Object component,
            final Context context) {
        final Class<?> dependencyClass = Annotations.isNotVoid(annotation.value()) ? annotation.value()
                : field.getType();
        setFieldValue(field, component, context.provide(dependencyClass));
    }

    /** @param field will be set.
     * @param component contains the field.
     * @param value will become field's value. */
    protected void setFieldValue(final Field field, final Object component, final Object value) {
        try {
            Reflection.setFieldValue(field, component, value);
        } catch (final ReflectionException exception) {
            throw new ContextInitiationException(
                    "Unable to inject value of field: " + field + " into component: " + component, exception);
        }
    }

    /** @param field needs a lazy wrapper injected.
     * @param annotation used to determine dependency and lazy types.
     * @param component owner of the field.
     * @param context used to resolve dependencies. */
    protected void processLazyInjection(final Field field, final Inject annotation, final Object component,
            final Context context) {
        final Class<?> dependencyClass = annotation.lazy();
        final ObjectProvider<?> provider = getLazyProvider(field, component, context, dependencyClass);
        setFieldValue(field, component, toLazy(provider, annotation));
    }

    /** @param field needs to be injected with a lazy wrapped.
     * @param component contains the field.
     * @param context used to resolve dependencies.
     * @param dependencyClass required class.
     * @return provider that provides instances of dependency class. */
    protected ObjectProvider<?> getLazyProvider(final Field field, final Object component, final Context context,
            final Class<?> dependencyClass) {
        if (context.isPresent(dependencyClass)) {
            return new ComponentProvider(context.getComponent(dependencyClass));
        } else if (context.isProviderPresentFor(dependencyClass)) {
            return context.getProvider(dependencyClass);
        } else if (context.isCreatingMissingDependencies()) {
            return ReflectionObjectProvider.forClass(dependencyClass);
        }
        throw new ContextInitiationException(
                "Unable to inject lazy value of field: " + field + " in component: " + component);
    }

    /** @param provider provides instances for lazy wrapper.
     * @param annotation used to determine lazy type.
     * @return lazy wrapper with the selected provider. */
    protected Lazy<Object> toLazy(final ObjectProvider<?> provider, final Inject annotation) {
        if (annotation.concurrentLazy()) {
            return Lazy.concurrentProvidedBy(provider);
        }
        return Lazy.providedBy(provider);
    }

    /** Provides component extracted from context.
     *
     * @author MJ */
    public static class ComponentProvider implements ObjectProvider<Object> {
        private final Object component;

        public ComponentProvider(final Object component) {
            this.component = component;
        }

        @Override
        public Object provide() {
            return component;
        }
    }
}
