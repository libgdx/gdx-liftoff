package com.github.czyzby.lml.parser.impl.attribute;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Any widget with a tree parent can be a tree node containing its own tree node children by setting this attribute to
 * true. This changes widget behavior: instead of appending text between tags as it normally would, it will create a
 * label and add it as a tree leaf; instead of appending children to itself, it will convert them to tree nodes. For
 * example:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;tree&gt;
 *      &lt;label node=true text="Node"&gt;
 *          &lt;textbutton&gt;Leaf.&lt;/textbutton&gt;
 *          Leaf.
 *      &lt;/label&gt;
 * &lt;/tree&gt;
 * </pre>
 *
 * Normally, label would append any text between tags as its own label text and throw an exception for any widget
 * children (such as text button in the example), as it cannot append any actors to itself. However, thanks to node
 * attribute set to true and being in a tree tag, it will instead have two leafs: text button and a default style label
 * created with "Leaf." text. This attribute expects a boolean. By default, it is mapped to "node" attribute names.
 *
 * </blockquote>
 *
 * @author MJ */
public class TreeNodeLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        LmlUtilities.getLmlUserObject(actor).prepareTreeNode(actor, tag.getParent(), parser);
    }
}
