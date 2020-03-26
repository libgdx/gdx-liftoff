package com.github.czyzby.lml.vis.parser.impl.attribute.window;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisWindow;

/** See {@link VisWindow#setKeepWithinParent(boolean)}. Expects a boolean. Mapped to "keepWithinParent".
 *
 * @author MJ */
public class KeepWithinParentLmlAttribute implements LmlAttribute<VisWindow> {
    @Override
    public Class<VisWindow> getHandledType() {
        return VisWindow.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisWindow actor,
            final String rawAttributeData) {
        actor.setKeepWithinParent(parser.parseBoolean(rawAttributeData, actor));
    }
}
