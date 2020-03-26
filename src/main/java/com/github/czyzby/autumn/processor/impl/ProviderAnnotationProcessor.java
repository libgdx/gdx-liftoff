package com.github.czyzby.autumn.processor.impl;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.github.czyzby.autumn.annotation.Provider;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.autumn.provider.DependencyProvider;
import com.github.czyzby.autumn.provider.impl.ReflectionDependencyProvider;
import com.github.czyzby.kiwi.util.gdx.reflection.Annotations;

/** Used to process {@link Provider} annotation. Registers providers in the context.
 *
 * @author MJ */
public class ProviderAnnotationProcessor extends AbstractAnnotationProcessor<Provider> {
    @Override
    public Class<Provider> getSupportedAnnotationType() {
        return Provider.class;
    }

    @Override
    public boolean isSupportingTypes() {
        return true;
    }

    @Override
    public void processType(final Class<?> type, final Provider annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        if (component instanceof DependencyProvider<?>) {
            context.addProvider((DependencyProvider<?>) component);
        } else {
            extractProviderMethods(component, context);
        }
    }

    /** @param component will have its methods in class tree extracted.
     * @param context will be used to resolve method dependencies. */
    protected void extractProviderMethods(final Object component, final Context context) {
        Class<?> componentClass = component.getClass();
        while (componentClass != null && !componentClass.equals(Object.class)) {
            final Method[] methods = ClassReflection.getDeclaredMethods(componentClass);
            if (methods != null && methods.length > 0) {
                convertToProviders(component, methods, context);
            }
            componentClass = componentClass.getSuperclass();
        }
    }

    /** @param component is the owner of the methods.
     * @param methods will be converted to dependency providers and added to context.
     * @param context will contain the providers. Used to resolve methods' dependencies. */
    protected void convertToProviders(final Object component, final Method[] methods, final Context context) {
        for (final Method method : methods) {
            final Class<?> returnType = method.getReturnType();
            if (Annotations.isNotVoid(returnType)) { // Not null, void or Void.
                context.addProvider(new ReflectionDependencyProvider(context, method, component));
            }
        }
    }
}
