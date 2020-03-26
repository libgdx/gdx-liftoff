package com.github.czyzby.lml.parser.impl.attribute.tree;

import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Tree#setPadding(float)}. Mapped to "treePad", "padding".
 *
 * @author MJ */
public class TreePaddingLmlAttribute implements LmlAttribute<Tree> {
    @Override
    public Class<Tree> getHandledType() {
        return Tree.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Tree actor, final String rawAttributeData) {
        actor.setPadding(parser.parseFloat(rawAttributeData, actor));
    }
}
