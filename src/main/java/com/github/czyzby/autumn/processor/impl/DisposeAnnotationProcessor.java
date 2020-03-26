package com.github.czyzby.autumn.processor.impl;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.reflect.Field;
import com.github.czyzby.autumn.annotation.Dispose;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.context.error.ContextInitiationException;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.common.Exceptions;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.Lazy;
import com.github.czyzby.kiwi.util.gdx.collection.disposable.DisposableArray;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Disposes of {@link Disposable} fields and components.
 *
 * @author MJ */
public class DisposeAnnotationProcessor extends AbstractAnnotationProcessor<Dispose> {
    private final DisposableArray<Disposable> disposables = DisposableArray.newArray();

    @Override
    public Class<Dispose> getSupportedAnnotationType() {
        return Dispose.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void processField(final Field field, final Dispose annotation, final Object component, final Context context,
            final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        disposables.add(new DisposableField(field, component));
    }

    @Override
    public boolean isSupportingTypes() {
        return true;
    }

    @Override
    public void processType(final Class<?> type, final Dispose annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        if (component instanceof Disposable) {
            disposables.add((Disposable) component);
        } else {
            throw new ContextInitiationException(
                    "Dispose annotation should annotate only disposable components and fields. " + type
                            + " is annotated, but does not seem to implement Disposable interface.");
        }
    }

    @Override
    public void doAfterScanning(final ContextInitializer initializer, final Context context,
            final ContextDestroyer destroyer) {
        destroyer.addAction(new DisposingAction(disposables));
    }

    /** Disposes of selected disposable objects.
     *
     * @author MJ */
    public static class DisposingAction implements Runnable {
        private final DisposableArray<Disposable> disposables;

        public DisposingAction(final DisposableArray<Disposable> disposables) {
            this.disposables = disposables;
        }

        @Override
        public void run() {
            disposables.dispose();
            disposables.clear();
        }
    }

    /** Allows to dispose of a field's value.
     *
     * @author MJ */
    public static class DisposableField implements Disposable {
        private final Field field;
        private final Object fieldOwner;

        public DisposableField(final Field field, final Object fieldOwner) {
            this.field = field;
            this.fieldOwner = fieldOwner;
        }

        @Override
        public void dispose() {
            try {
                final Object value = Reflection.getFieldValue(field, fieldOwner);
                if (value instanceof Disposable) {
                    ((Disposable) value).dispose();
                } else if (value instanceof Lazy<?>) {
                    final Lazy<?> lazy = (Lazy<?>) value;
                    if (lazy.isInitialized() && lazy.get() instanceof Disposable) {
                        ((Disposable) lazy.get()).dispose();
                    }
                }
            } catch (final Exception exception) {
                Exceptions.ignore(exception); // Ignored. Closing the application, invalid objects can occur.
            }
        }
    }
}
