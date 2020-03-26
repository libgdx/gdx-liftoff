package com.github.czyzby.autumn.mvc.component.ui.action;

import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** LML view action. Exits the application on invocation.
 *
 * @author MJ */
public class ApplicationExitAction implements ActorConsumer<Void, Object> {
    /** Name of the action as it appears in the templates. Can be changed globally before the context loading. */
    public static String ID = "app:exit";
    private final InterfaceService interfaceService;

    public ApplicationExitAction(final InterfaceService interfaceService) {
        this.interfaceService = interfaceService;
    }

    @Override
    public Void consume(final Object actor) {
        interfaceService.exitApplication();
        return null;
    }
}
