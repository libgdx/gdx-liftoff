package com.github.czyzby.lml.vis.parser.impl.attribute.draggable.pane;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.layout.DragPane;

/** Expects an int. Using {@link DragPane#setListener(DragPane.DragPaneListener)}, this
 * attribute attaches a listener that will reject dragged actors if the {@link DragPane} already has the specified
 * children amount. This overrides any previous listeners. Mapped to "maxChildren".
 *
 * @author MJ */
public class MaxChildrenLmlAttribute implements LmlAttribute<DragPane> {
    @Override
    public Class<DragPane> getHandledType() {
        return DragPane.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final DragPane actor, final String rawAttributeData) {
        actor.setListener(new DragPane.DragPaneListener.LimitChildren(parser.parseInt(rawAttributeData, actor)));
    }
}
