package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.GenericTreeNode;
import com.github.czyzby.lml.util.LmlUtilities;

/** Handles {@link Tree} actor. Allows the use of "node" attribute in children tags. Adds plain text between tags as new
 * label nodes. Mapped to "tree".
 *
 * @author MJ
 * @see com.github.czyzby.lml.parser.impl.attribute.TreeNodeLmlAttribute */
public class TreeLmlTag extends AbstractActorLmlTag {
    public TreeLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new Tree(getSkin(builder), builder.getStyleName());
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        addChild(childTag.getActor());
    }

    private void addChild(final Actor child) {
        final Tree.Node node = LmlUtilities.getTreeNode(child);
        if (node != null) {
            getTree().add(node);
        } else {
            getTree().add(new GenericTreeNode(child));
        }
    }

    /** @return casted actor. */
    protected Tree getTree() {
        return (Tree) getActor();
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        addChild(toLabel(plainTextLine));
    }
}
