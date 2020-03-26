package com.github.czyzby.lml.parser.impl.attribute.listener;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractListenerLmlTag;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.ActorStorage;

/** Base for {@link AbstractListenerLmlTag} attributes.
 *
 * @author MJ */
public abstract class AbstractListenerLmlAttribute implements LmlAttribute<ActorStorage> {
    @Override
    public Class<ActorStorage> getHandledType() {
        return ActorStorage.class;
    }

    @Override
    public final void process(final LmlParser parser, final LmlTag tag, final ActorStorage actor,
            final String rawAttributeData) {
        if (tag instanceof AbstractListenerLmlTag) {
            process(parser, (AbstractListenerLmlTag) tag, actor, rawAttributeData);
        } else {
            parser.throwErrorIfStrict(
                    "This attribute can be added only to listener tags. Passed tag does not process a listener: "
                            + tag);
        }
    }

    /** @param parser handles LML template parsing.
     * @param tag contains raw tag data. Manages listener creation and events handling.
     * @param actor handled actor instance, casted for convenience.
     * @param rawAttributeData unparsed LML attribute data that should be handled by this attribute processor. Common
     *            data types (string, int, float, boolean, action) are already handled by LML parser implementation, so
     *            make sure to invoke its methods. */
    protected abstract void process(LmlParser parser, AbstractListenerLmlTag tag, ActorStorage actor,
            String rawAttributeData);
}
