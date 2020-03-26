package com.github.czyzby.lml.parser.tag;

import com.github.czyzby.lml.parser.LmlParser;

/** Represents a single attribute processor, maintaining one property of a single widget type.
 *
 * @author MJ
 *
 * @param <Actor> base type of handled widgets. Attribute processors are always chosen by their most specific type, so -
 *            for example - TextButton attribute will be chosen over Table attribute parser if a TextButton is being
 *            handled. */
public interface LmlAttribute<Actor> {
    /** @return base actor class that can be handled by this attribute processor. */
    Class<Actor> getHandledType();

    /** @param parser handles LML template parsing.
     * @param tag contains raw tag data. Allows to access actor's parent.
     * @param actor handled actor instance, casted for convenience.
     * @param rawAttributeData unparsed LML attribute data that should be handled by this attribute processor. Common
     *            data types (string, int, float, boolean, action) are already handled by LML parser implementation, so
     *            make sure to invoke its methods. */
    void process(LmlParser parser, LmlTag tag, Actor actor, String rawAttributeData);
}
