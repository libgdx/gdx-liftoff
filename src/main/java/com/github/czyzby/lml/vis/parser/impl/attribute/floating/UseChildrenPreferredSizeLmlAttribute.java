package com.github.czyzby.lml.vis.parser.impl.attribute.floating;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.layout.FloatingGroup;

/** See {@link FloatingGroup#setUseChildrenPreferredSize(boolean)}. If set to false, it allows to explicitly set sizes
 * of children with width, height and size attributes, and these settings will be honored over actors' reported
 * preferred sizes. Defaults to false. Mapped to "useChildrenPreferredSize", "usePref".
 *
 * @author MJ */
public class UseChildrenPreferredSizeLmlAttribute implements LmlAttribute<FloatingGroup> {
    @Override
    public Class<FloatingGroup> getHandledType() {
        return FloatingGroup.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final FloatingGroup actor,
            final String rawAttributeData) {
        actor.setUseChildrenPreferredSize(parser.parseBoolean(rawAttributeData, actor));
    }
}
