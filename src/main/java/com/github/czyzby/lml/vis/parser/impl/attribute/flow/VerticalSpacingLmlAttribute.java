package com.github.czyzby.lml.vis.parser.impl.attribute.flow;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.layout.VerticalFlowGroup;

/** See {@link VerticalFlowGroup#setSpacing(float)}. Mapped to "spacing".
 *
 * @author MJ */
public class VerticalSpacingLmlAttribute implements LmlAttribute<VerticalFlowGroup> {
    @Override
    public Class<VerticalFlowGroup> getHandledType() {
        return VerticalFlowGroup.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VerticalFlowGroup actor,
            final String rawAttributeData) {
        actor.setSpacing(parser.parseFloat(rawAttributeData, actor));
    }
}
