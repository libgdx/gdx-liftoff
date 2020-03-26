package com.github.czyzby.lml.parser.action;

/** A simple interface that allows to assign actions to actors.
 *
 * @author MJ */
public interface ActorConsumer<ReturnType, Widget> {
    /** @param actor triggered when a specified action concerning this actor was fired.
     * @return value returned by the action. Might be null, if no result is expected. */
    ReturnType consume(Widget actor);
}
