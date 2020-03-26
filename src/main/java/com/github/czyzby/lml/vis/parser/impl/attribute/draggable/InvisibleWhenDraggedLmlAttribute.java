package com.github.czyzby.lml.vis.parser.impl.attribute.draggable;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.Draggable;

/** See {@link Draggable#setInvisibleWhenDragged(boolean)}. Mapped to "invisible", "invisibleWhenDragged".
 *
 * @author MJ */
public class InvisibleWhenDraggedLmlAttribute implements LmlAttribute<Draggable> {
    @Override
    public Class<Draggable> getHandledType() {
        return Draggable.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Draggable actor,
            final String rawAttributeData) {
        actor.setInvisibleWhenDragged(parser.parseBoolean(rawAttributeData, actor));
    }
}
