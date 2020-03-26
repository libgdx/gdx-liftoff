package com.github.czyzby.lml.vis.parser.impl.attribute.split;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisSplitPane;

/** See {@link VisSplitPane#setMaxSplitAmount(float)}. By default, mapped to "max", "maxSplit", "maxSplitAmount".
 *
 * @author MJ */
public class MaxSplitLmlAttribute implements LmlAttribute<VisSplitPane> {
    @Override
    public Class<VisSplitPane> getHandledType() {
        return VisSplitPane.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisSplitPane actor,
            final String rawAttributeData) {
        actor.setMaxSplitAmount(parser.parseFloat(rawAttributeData, actor));
    }
}
