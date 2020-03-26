package com.github.czyzby.lml.vis.parser.impl.attribute.draggable;

import com.badlogic.gdx.math.Interpolation;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.Draggable;

/** See {@link Draggable#setFadingInterpolation(Interpolation)}. Expects an action ID of a method that consumes
 * {@link Draggable} and returns an {@link Interpolation}. Mapped to "fadingInterpolation".
 *
 * @author MJ */
public class DraggedFadingInterpolationLmlAttribute implements LmlAttribute<Draggable> {
    @Override
    public Class<Draggable> getHandledType() {
        return Draggable.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Draggable actor,
            final String rawAttributeData) {
        @SuppressWarnings("unchecked") final ActorConsumer<Interpolation, Draggable> action = (ActorConsumer<Interpolation, Draggable>) parser
                .parseAction(rawAttributeData, actor);
        if (action == null) {
            parser.throwErrorIfStrict(
                    "Draggable interpolation attribute expects an action ID that references method consuming Draggable and returning Interpolation. Method not found for ID: "
                            + rawAttributeData);
            return;
        }
        actor.setFadingInterpolation(action.consume(actor));
    }
}
