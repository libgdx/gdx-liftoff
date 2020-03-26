package com.github.czyzby.lml.vis.parser.impl.attribute.collapsible;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;

/** See {@link CollapsibleWidget#setCollapsed(boolean, boolean)}. Invoked without collapsing animation. Mapped to
 * "collapse", "collapsed".
 *
 * @author MJ */
public class CollapsedLmlAttribute implements LmlAttribute<CollapsibleWidget> {
    @Override
    public Class<CollapsibleWidget> getHandledType() {
        return CollapsibleWidget.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final CollapsibleWidget actor,
            final String rawAttributeData) {
        actor.setCollapsed(parser.parseBoolean(rawAttributeData, actor), false); // false - no animation.
    }
}
