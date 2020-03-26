package com.github.czyzby.lml.parser.impl.attribute.listener;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractListenerLmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.ActorStorage;

/** See {@link AbstractListenerLmlTag#setCondition(String)}. Expends an equation that can be solved to a single positive
 * or negative result. Mapped to "if".
 *
 * @author MJ */
public class ConditionLmlAttribute extends AbstractListenerLmlAttribute {
    @Override
    protected void process(final LmlParser parser, final AbstractListenerLmlTag tag, final ActorStorage actor,
            final String rawAttributeData) {
        tag.setCondition(rawAttributeData);
    }
}
