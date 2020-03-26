package com.github.czyzby.lml.parser;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

/** A simple functional interface that can be attached to {@link LmlParser} events.
 *
 * @author MJ */
public interface LmlParserListener {
    /** Return this value in {@link #onEvent(LmlParser, Array)} method for code clarity. */
    boolean KEEP = true, REMOVE = false;

    /** Invoked when the listened event occurs.
     *
     * @param parser triggered the event.
     * @param parsingResult this is the {@link Array} instance containing parsed root actors. If the listener listens to
     *            pre-parsing events, this array will be empty. If the listener listens to post-parsing events, the
     *            array will contain parsed root actors. This allows to directly modify (expand or filter) the returned
     *            result. Note that actors currently mapped by their IDs are available through
     *            {@link LmlParser#getActorsMappedByIds()}.
     * @return if true, event will kept after execution and invoked during the next parsing. If false, listener will be
     *         removed after invocation.
     * @see LmlParserListener#KEEP
     * @see LmlParserListener#REMOVE */
    boolean onEvent(LmlParser parser, Array<Actor> parsingResult);
}
