package com.github.czyzby.lml.vis.parser.impl.attribute.draggable.pane;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.layout.DragPane;

/** Expects a boolean. If boolean value is false, {@link DragPane.DragPaneListener.AcceptOwnChildren} will be attached
 * with {@link DragPane#setListener(DragPane.DragPaneListener)} method, prohibiting foreign
 * actors from being added to the pane. This overrides any previous listeners. Mapped to "foreign", "acceptForeign".
 *
 * @author MJ */
public class AcceptForeignLmlAttribute implements LmlAttribute<DragPane> {
    @Override
    public Class<DragPane> getHandledType() {
        return DragPane.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final DragPane actor, final String rawAttributeData) {
        if (!parser.parseBoolean(rawAttributeData, actor)) {
            actor.setListener(new DragPane.DragPaneListener.AcceptOwnChildren());
        }
    }
}
