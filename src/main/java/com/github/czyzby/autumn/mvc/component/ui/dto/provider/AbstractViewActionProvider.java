package com.github.czyzby.autumn.mvc.component.ui.dto.provider;

import com.badlogic.gdx.utils.ObjectSet;
import com.github.czyzby.kiwi.util.gdx.collection.GdxSets;
import com.github.czyzby.lml.parser.LmlParser;

/** Base implementation for {@link ViewActionProvider} that
 * manages handled view types.
 *
 * @author MJ */
public abstract class AbstractViewActionProvider implements ViewActionProvider {
    private final ObjectSet<String> viewIds;

    public AbstractViewActionProvider(final String[] viewIds) {
        this.viewIds = GdxSets.newSet(viewIds);
    }

    @Override
    public void register(final LmlParser parser, final String viewId) {
        if (viewIds.contains(viewId)) {
            register(parser);
        }
    }

    /** The view ID is valid.
     *
     * @param parser should have the action registered. */
    protected abstract void register(LmlParser parser);

    @Override
    public void unregister(final LmlParser parser, final String viewId) {
        if (viewIds.contains(viewId)) {
            unregister(parser);
        }
    }

    /** The view ID is valid.
     *
     * @param parser should have the action removed. */
    protected abstract void unregister(LmlParser parser);
}
