package com.github.czyzby.lml.vis.parser.impl.attribute.flow;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;

/** See {@link HorizontalFlowGroup#setSpacing(float)}. Mapped to "spacing".
 *
 * @author MJ */
public class HorizontalSpacingLmlAttribute implements LmlAttribute<HorizontalFlowGroup> {
    @Override
    public Class<HorizontalFlowGroup> getHandledType() {
        return HorizontalFlowGroup.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final HorizontalFlowGroup actor,
            final String rawAttributeData) {
        actor.setSpacing(parser.parseFloat(rawAttributeData, actor));
    }
}
