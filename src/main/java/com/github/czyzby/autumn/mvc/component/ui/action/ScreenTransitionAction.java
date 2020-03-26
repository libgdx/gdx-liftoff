package com.github.czyzby.autumn.mvc.component.ui.action;

import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** An action that represents screen transition. Changes current view on invocation.
 *
 * @author MJ */
public class ScreenTransitionAction implements ActorConsumer<Void, Object> {
    private final InterfaceService interfaceService;
    private final Class<?> viewControllerClass;

    public ScreenTransitionAction(final InterfaceService interfaceService, final Class<?> viewControllerClass) {
        this.interfaceService = interfaceService;
        this.viewControllerClass = viewControllerClass;
    }

    @Override
    public Void consume(final Object actor) {
        interfaceService.show(viewControllerClass);
        return null;
    }
}
