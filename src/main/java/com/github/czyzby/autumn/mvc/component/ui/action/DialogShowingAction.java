package com.github.czyzby.autumn.mvc.component.ui.action;

import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** An action that represents dialog showing. Displays chosen dialog on invocation.
 *
 * @author MJ */
public class DialogShowingAction implements ActorConsumer<Void, Object> {
    private final InterfaceService interfaceService;
    private final Class<?> viewDialogControllerClass;

    public DialogShowingAction(final InterfaceService interfaceService, final Class<?> viewDialogControllerClass) {
        this.interfaceService = interfaceService;
        this.viewDialogControllerClass = viewDialogControllerClass;
    }

    @Override
    public Void consume(final Object actor) {
        interfaceService.showDialog(viewDialogControllerClass);
        return null;
    }
}