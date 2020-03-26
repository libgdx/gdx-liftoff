package com.github.czyzby.lml.parser.impl.attribute.input;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link TextField#setMaxLength(int)}. Mapped to "max", "maxLength".
 *
 * @author MJ */
public class MaxLengthLmlAttribute implements LmlAttribute<TextField> {
    @Override
    public Class<TextField> getHandledType() {
        return TextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final TextField actor,
            final String rawAttributeData) {
        actor.setMaxLength(parser.parseInt(rawAttributeData, actor));
    }
}
