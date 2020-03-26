package com.github.czyzby.autumn.mvc.component.ui.processor;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Field;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.impl.AbstractAnnotatedController;
import com.github.czyzby.autumn.mvc.stereotype.ViewStage;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;

/** Used to process fields that should have {@link Stage} injected.
 *
 * @author MJ */
public class ViewStageAnnotationProcessor extends AbstractAnnotationProcessor<ViewStage> {
    @Inject private InterfaceService interfaceService;

    @Override
    public Class<ViewStage> getSupportedAnnotationType() {
        return ViewStage.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void processField(final Field field, final ViewStage annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        if (!Stage.class.equals(field.getType())) {
            throw new GdxRuntimeException("Only Scene2D stages can be annotated with @ViewStage. Found type:"
                    + field.getType() + " in field: " + field + " of component: " + component + ".");
        }
        final Class<?> controllerClass = component.getClass();
        if (!registerField(field, interfaceService.getController(controllerClass))) {
            // If view controller not found, trying out dialog controllers:
            if (!registerField(field, interfaceService.getDialogController(controllerClass))) {
                throw new GdxRuntimeException(
                        "Unable to assign stage in field: " + field + " of component: " + component + ".");
            }
        }
    }

    private static boolean registerField(final Field field, final Object controller) {
        if (controller instanceof AbstractAnnotatedController) {
            ((AbstractAnnotatedController) controller).registerStageField(field);
            return true;
        }
        return false;
    }
}
