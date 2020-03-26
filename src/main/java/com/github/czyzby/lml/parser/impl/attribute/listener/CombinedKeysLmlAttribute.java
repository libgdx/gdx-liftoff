package com.github.czyzby.lml.parser.impl.attribute.listener;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractListenerLmlTag;
import com.github.czyzby.lml.parser.impl.tag.listener.InputListenerLmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.ActorStorage;

/** See {@link InputListenerLmlTag#setCombined(boolean)}. Expects a boolean. Mapped to "combined".
 *
 * @author MJ */
public class CombinedKeysLmlAttribute extends AbstractListenerLmlAttribute {
    @Override
    protected void process(final LmlParser parser, final AbstractListenerLmlTag tag, final ActorStorage actor,
            final String rawAttributeData) {
        if (tag instanceof InputListenerLmlTag) {
            ((InputListenerLmlTag) tag).setCombined(parser.parseBoolean(rawAttributeData, actor));
        } else {
            parser.throwErrorIfStrict("'combined' attribute can be used only for input listeners.");
        }
    }
}
