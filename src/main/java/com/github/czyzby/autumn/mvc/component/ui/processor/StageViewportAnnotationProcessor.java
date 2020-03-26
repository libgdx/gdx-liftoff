package com.github.czyzby.autumn.mvc.component.ui.processor;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.stereotype.preference.StageViewport;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Used to scan for viewport provider.
 *
 * @author MJ */
public class StageViewportAnnotationProcessor extends AbstractAnnotationProcessor<StageViewport> {
    @Inject private InterfaceService interfaceService;

    @Override
    public Class<StageViewport> getSupportedAnnotationType() {
        return StageViewport.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void processField(final Field field, final StageViewport annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        try {
            final Object provider = Reflection.getFieldValue(field, component);
            setViewportProvider(provider);
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException("Unable to extract viewport provider.", exception);
        }
    }

    @SuppressWarnings("unchecked")
    private void setViewportProvider(final Object provider) {
        if (provider instanceof ObjectProvider<?>) {
            interfaceService.setViewportProvider((ObjectProvider<Viewport>) provider);
            return;
        }
        throw new GdxRuntimeException(
                "Invalid viewport provider: has to implement ObjectProvider<Viewport>: " + provider);
    }

    @Override
    public boolean isSupportingTypes() {
        return true;
    }

    @Override
    public void processType(final Class<?> type, final StageViewport annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        setViewportProvider(component);
    }
}