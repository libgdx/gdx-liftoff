package com.github.czyzby.autumn.mvc.component.ui.processor;

import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewController;
import com.github.czyzby.autumn.mvc.component.ui.controller.impl.AnnotatedViewController;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;

/** Processes {@link View} components. Initiates controllers.
 *
 * @author MJ */
public class ViewAnnotationProcessor extends AbstractAnnotationProcessor<View> {
    @Inject private InterfaceService interfaceService;
    @Inject private AssetService assetService;

    @Override
    public Class<View> getSupportedAnnotationType() {
        return View.class;
    }

    @Override
    public boolean isSupportingTypes() {
        return true;
    }

    @Override
    public void processType(final Class<?> type, final View annotation, final Object component, final Context context,
            final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        if (component instanceof ViewController) {
            interfaceService.registerController(type, (ViewController) component);
        } else {
            interfaceService.registerController(type, new AnnotatedViewController(annotation, component, assetService));
        }
    }
}
