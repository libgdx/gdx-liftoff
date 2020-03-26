package com.github.czyzby.lml.parser.impl.attribute.group;

import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link VerticalGroup#padBottom(float)}. Mapped to "groupPadBottom".
 *
 * @author MJ */
public class VerticalGroupPaddingBottomLmlAttribute implements LmlAttribute<VerticalGroup> {
    @Override
    public Class<VerticalGroup> getHandledType() {
        return VerticalGroup.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VerticalGroup actor,
            final String rawAttributeData) {
        actor.padBottom(parser.parseFloat(rawAttributeData, actor));
    }
}
