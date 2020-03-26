package com.github.czyzby.lml.parser.impl.attribute.tree;

import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Tree#setIconSpacing(float, float)}. Since this method sets both left and right spacings, providing no
 * getters for the actual values, it is currently not possible to set different values for spacing solely with LML
 * attributes; if you need to modify these, use a on create or on tag close callback method that will consume the Tree
 * actor once its constructed. Mapped to "iconSpacing", "iconSpace".
 *
 * @author MJ */
public class IconSpacingLmlAttribute implements LmlAttribute<Tree> {
    @Override
    public Class<Tree> getHandledType() {
        return Tree.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Tree actor, final String rawAttributeData) {
        final float spacing = parser.parseFloat(rawAttributeData, actor);
        actor.setIconSpacing(spacing, spacing);
    }
}
