package com.github.czyzby.lml.parser.impl.attribute.listener;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractListenerLmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.ActorStorage;

/** See {@link AbstractListenerLmlTag#setIds(String[])}. Expects an array of extra actor IDs that should have the
 * listener attached. Mapped to "ids".
 *
 * @author MJ */
public class ListenerIdsLmlAttribute extends AbstractListenerLmlAttribute {
    @Override
    protected void process(final LmlParser parser, final AbstractListenerLmlTag tag, final ActorStorage actor,
            final String rawAttributeData) {
        tag.setIds(parser.parseArray(rawAttributeData, actor));
    }
}
