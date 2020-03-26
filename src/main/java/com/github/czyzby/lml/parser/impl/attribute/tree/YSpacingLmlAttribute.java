package com.github.czyzby.lml.parser.impl.attribute.tree;

import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Tree#setYSpacing(float)}. Mapped to "ySpacing", "ySpace".
 *
 * @author MJ */
public class YSpacingLmlAttribute implements LmlAttribute<Tree> {
    @Override
    public Class<Tree> getHandledType() {
        return Tree.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Tree actor, final String rawAttributeData) {
        actor.setYSpacing(parser.parseFloat(rawAttributeData, actor));
    }
}
