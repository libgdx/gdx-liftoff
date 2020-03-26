package com.github.czyzby.lml.parser.impl.attribute.split;

import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link SplitPane#setMaxSplitAmount(float)}. By default, mapped to "max", "maxSplit", "maxSplitAmount".
 *
 * @author MJ */
public class MaxSplitLmlAttribute implements LmlAttribute<SplitPane> {
    @Override
    public Class<SplitPane> getHandledType() {
        return SplitPane.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final SplitPane actor,
            final String rawAttributeData) {
        actor.setMaxSplitAmount(parser.parseFloat(rawAttributeData, actor));
    }
}
