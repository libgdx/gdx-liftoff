package com.github.czyzby.autumn.mvc.component.ui.dto.provider;

import com.github.czyzby.lml.parser.LmlParser;

/** Wraps around a single {@link com.github.czyzby.lml.parser.action.ActionContainer} or
 * {@link com.github.czyzby.lml.parser.action.ActorConsumer}, providing access methods.
 *
 * @author MJ */
public interface ViewActionProvider {
    /** @param parser will contain the action(s).
     * @param viewId provider will decide whether the action(s) should be registered according to this ID. */
    void register(LmlParser parser, String viewId);

    /** @param parser will have the action removed.
     * @param viewId provider will decide whether the action(s) should be removed according to this ID. */
    void unregister(LmlParser parser, String viewId);
}