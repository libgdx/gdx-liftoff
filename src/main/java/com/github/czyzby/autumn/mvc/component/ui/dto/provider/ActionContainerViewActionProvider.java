package com.github.czyzby.autumn.mvc.component.ui.dto.provider;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActionContainer;

/** Wraps around an {@link com.github.czyzby.lml.parser.action.ActionContainer}.
 *
 * @author MJ */
public class ActionContainerViewActionProvider extends AbstractViewActionProvider {
    private final String id;
    private final ActionContainer actionContainer;

    public ActionContainerViewActionProvider(final String id, final ActionContainer actionContainer,
            final String[] viewIds) {
        super(viewIds);
        this.id = id;
        this.actionContainer = actionContainer;
    }

    @Override
    protected void register(final LmlParser parser) {
        parser.getData().addActionContainer(id, actionContainer);
    }

    @Override
    protected void unregister(final LmlParser parser) {
        parser.getData().removeActionContainer(id);
    }
}