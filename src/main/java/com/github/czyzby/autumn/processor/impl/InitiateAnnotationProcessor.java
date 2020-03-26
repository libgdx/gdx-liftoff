package com.github.czyzby.autumn.processor.impl;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.Method;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.context.impl.method.MethodInvocation;
import com.github.czyzby.autumn.context.impl.method.PrioritizedMethodInvocation;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;

/** Invokes {@link Initiate}-annotated methods after the context is fully built.
 *
 * @author MJ */
public class InitiateAnnotationProcessor extends AbstractAnnotationProcessor<Initiate> {
    private final Array<PrioritizedMethodInvocation> methods = GdxArrays.newArray();

    @Override
    public Class<Initiate> getSupportedAnnotationType() {
        return Initiate.class;
    }

    @Override
    public boolean isSupportingMethods() {
        return true;
    }

    @Override
    public void processMethod(final Method method, final Initiate annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        methods.add(new PrioritizedMethodInvocation(method, component,
                MethodInvocation.getParametersFromContext(method.getParameterTypes(), context), annotation.priority()));
    }

    @Override
    public void doAfterScanning(final ContextInitializer initializer, final Context context,
            final ContextDestroyer destroyer) {
        methods.sort();
        for (final PrioritizedMethodInvocation method : methods) {
            method.invoke();
        }
        methods.clear();
    }
}
