package com.github.czyzby.lml.scene2d.ui.reflected;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;

/** Since {@link Tree.Node} is an abstract class, we need a generic implementation to create tree nodes.
 *
 * @author metaphore */
public class GenericTreeNode extends Tree.Node<GenericTreeNode, Object, Actor> {

    public GenericTreeNode(Actor actor) {
        super(actor);
    }

    public GenericTreeNode() {
        super();
    }
}
