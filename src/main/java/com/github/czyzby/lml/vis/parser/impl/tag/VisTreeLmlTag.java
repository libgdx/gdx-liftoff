package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.actor.TreeLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisTree;

/** Handles {@link Tree} actor. Allows the use of "node" attribute in children tags. Adds plain text between tags as new
 * label nodes. Mapped to "tree", "visTree".
 *
 * @author Kotcrab
 * @see com.github.czyzby.lml.parser.impl.attribute.TreeNodeLmlAttribute */
public class VisTreeLmlTag extends TreeLmlTag {
    public VisTreeLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new VisTree(builder.getStyleName());
    }
}
