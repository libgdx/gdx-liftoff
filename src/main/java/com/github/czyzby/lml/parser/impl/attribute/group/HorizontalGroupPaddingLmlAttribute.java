package com.github.czyzby.lml.parser.impl.attribute.group;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link HorizontalGroup#pad(float)}. Mapped to "groupPad", "padding".
 *
 * @author MJ */
public class HorizontalGroupPaddingLmlAttribute implements LmlAttribute<HorizontalGroup> {
    @Override
    public Class<HorizontalGroup> getHandledType() {
        return HorizontalGroup.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final HorizontalGroup actor,
            final String rawAttributeData) {
        actor.pad(parser.parseFloat(rawAttributeData, actor));
    }
}
