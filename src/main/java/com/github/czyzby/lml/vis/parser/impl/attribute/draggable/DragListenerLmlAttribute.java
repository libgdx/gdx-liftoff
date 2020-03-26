package com.github.czyzby.lml.vis.parser.impl.attribute.draggable;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.Draggable;
import com.kotcrab.vis.ui.widget.Draggable.DragListener;

/** See {@link Draggable#setListener(DragListener)}. Expects an action ID of a method that consumes {@link Draggable}
 * and returns a {@link DragListener}. Mapped to "listener".
 *
 * @author MJ */
public class DragListenerLmlAttribute implements LmlAttribute<Draggable> {
    @Override
    public Class<Draggable> getHandledType() {
        return Draggable.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Draggable actor,
            final String rawAttributeData) {
        @SuppressWarnings("unchecked") final ActorConsumer<DragListener, Draggable> action = (ActorConsumer<DragListener, Draggable>) parser
                .parseAction(rawAttributeData, actor);
        if (action == null) {
            parser.throwErrorIfStrict(
                    "Draggable listener attribute expects an action ID that references method consuming Draggable and returning DragListener. Method not found for ID: "
                            + rawAttributeData);
            return;
        }
        actor.setListener(action.consume(actor));
    }
}
