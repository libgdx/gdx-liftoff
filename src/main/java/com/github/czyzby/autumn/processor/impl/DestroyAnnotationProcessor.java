package com.github.czyzby.autumn.processor.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.Method;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.context.impl.method.MethodInvocation;
import com.github.czyzby.autumn.context.impl.method.PrioritizedMethodInvocation;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;

/** Invokes {@link Destroy}-annotated methods on context destruction.
 *
 * @author MJ */
public class DestroyAnnotationProcessor extends AbstractAnnotationProcessor<Destroy> {
    private final Array<PrioritizedMethodInvocation> methods = GdxArrays.newArray();

    @Override
    public Class<Destroy> getSupportedAnnotationType() {
        return Destroy.class;
    }

    @Override
    public boolean isSupportingMethods() {
        return true;
    }

    @Override
    public void processMethod(final Method method, final Destroy annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        methods.add(new PrioritizedMethodInvocation(method, component,
                MethodInvocation.getParametersFromContext(method.getParameterTypes(), context), annotation.priority()));
    }

    @Override
    public void doAfterScanning(final ContextInitializer initializer, final Context context,
            final ContextDestroyer destroyer) {
        methods.sort();
        destroyer.addAction(new DestructionRunnable(methods));
    }

    /** Invokes passed methods. Expects that the methods are already sorted.
     *
     * @author MJ */
    public static class DestructionRunnable implements Runnable {
        private final Array<PrioritizedMethodInvocation> methods;

        public DestructionRunnable(final Array<PrioritizedMethodInvocation> methods) {
            this.methods = methods;
        }

        @Override
        public void run() {
            for (final PrioritizedMethodInvocation method : methods) {
                // We want to invoke all methods to make sure that all resources are closed, even if some fail - hence
                // try-catch.
                try {
                    method.invoke();
                } catch (final Exception exception) {
                    if (Gdx.app != null) {
                        Gdx.app.error("WARN", "Unable to invoke destruction method.", exception);
                    }
                }
            }
            methods.clear();
        }
    }
}
