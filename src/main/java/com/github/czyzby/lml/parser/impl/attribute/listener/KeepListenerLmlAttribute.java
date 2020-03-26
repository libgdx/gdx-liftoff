package com.github.czyzby.lml.parser.impl.attribute.listener;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractListenerLmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.ActorStorage;

/** See {@link AbstractListenerLmlTag#setKeepListener(boolean)}. Allows to share the listener across multiple templates
 * (views). Expects a boolean. Mapped to "keep".
 *
 * @author MJ */
public class KeepListenerLmlAttribute extends AbstractListenerLmlAttribute {
    @Override
    protected void process(final LmlParser parser, final AbstractListenerLmlTag tag, final ActorStorage actor,
            final String rawAttributeData) {
        tag.setKeepListener(parser.parseBoolean(rawAttributeData, actor));
    }
}
