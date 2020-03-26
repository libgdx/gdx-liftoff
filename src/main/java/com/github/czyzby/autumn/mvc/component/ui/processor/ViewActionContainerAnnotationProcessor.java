package com.github.czyzby.autumn.mvc.component.ui.processor;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.dto.provider.ActionContainerViewActionProvider;
import com.github.czyzby.autumn.mvc.component.ui.dto.provider.ActorConsumerViewActionProvider;
import com.github.czyzby.autumn.mvc.component.ui.dto.provider.ViewActionProvider;
import com.github.czyzby.autumn.mvc.stereotype.ViewActionContainer;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** Registers action containers and actor consumers in the interface service.
 *
 * @author MJ */
public class ViewActionContainerAnnotationProcessor extends AbstractAnnotationProcessor<ViewActionContainer> {
    @Inject private InterfaceService interfaceService;

    private final Array<ViewActionProvider> actionProviders = GdxArrays.newArray();

    @Override
    public Class<ViewActionContainer> getSupportedAnnotationType() {
        return ViewActionContainer.class;
    }

    @Override
    public boolean isSupportingTypes() {
        return true;
    }

    @Override
    public void processType(final Class<?> type, final ViewActionContainer annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        if (isGlobal(annotation)) {
            registerGlobalAction(annotation.value(), component);
        } else {
            registerLocalizedAction(annotation.value(), annotation.views(), component);
        }
    }

    /** @return true if exclusive views amount equals 0. */
    private static boolean isGlobal(final ViewActionContainer actionData) {
        return actionData.views().length == 0;
    }

    private void registerGlobalAction(final String id, final Object actionContainer) {
        if (actionContainer instanceof ActionContainer) {
            interfaceService.addViewActionContainer(id, (ActionContainer) actionContainer);
        } else if (actionContainer instanceof ActorConsumer) {
            interfaceService.addViewAction(id, (ActorConsumer<?, ?>) actionContainer);
        } else {
            throw new GdxRuntimeException("Invalid type annotated with ViewActionContainer: " + actionContainer);
        }
    }

    private void registerLocalizedAction(final String actionId, final String[] viewIds, final Object actionContainer) {
        if (actionContainer instanceof ActionContainer) {
            actionProviders
                    .add(new ActionContainerViewActionProvider(actionId, (ActionContainer) actionContainer, viewIds));
        } else if (actionContainer instanceof ActorConsumer) {
            actionProviders
                    .add(new ActorConsumerViewActionProvider(actionId, (ActorConsumer<?, ?>) actionContainer, viewIds));
        } else {
            throw new GdxRuntimeException("Invalid type annotated with ViewActionContainer: " + actionContainer);
        }
    }

    /** @return all registered actions that should be available only for specific views. */
    public Array<ViewActionProvider> getActionProviders() {
        return actionProviders;
    }
}