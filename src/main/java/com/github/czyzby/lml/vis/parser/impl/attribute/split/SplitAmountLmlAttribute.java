package com.github.czyzby.lml.vis.parser.impl.attribute.split;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisSplitPane;

/** See {@link VisSplitPane#setSplitAmount(float)}. By default, mapped to "split", "splitAmount", "value".
 *
 * @author MJ */
public class SplitAmountLmlAttribute implements LmlAttribute<VisSplitPane> {
    @Override
    public Class<VisSplitPane> getHandledType() {
        return VisSplitPane.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisSplitPane actor,
            final String rawAttributeData) {
        actor.setSplitAmount(parser.parseFloat(rawAttributeData, actor));
    }
}
