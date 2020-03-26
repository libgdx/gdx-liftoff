package com.github.czyzby.lml.vis.parser.impl.attribute.draggable.pane;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.layout.DragPane;
import com.kotcrab.vis.ui.layout.DragPane.DragPaneListener;

/** See {@link DragPane#setListener(DragPaneListener)}. Expends ID of an action that references method consuming an
 * {@link Actor} and returning boolean (boxed or primitive). If the returned value is true, actor will be accepted and
 * anded into the {@link DragPane}; otherwise the actor will return to its original position. Mapped to "listener".
 *
 * @author MJ
 * @see DragPaneListener */
public class DragPaneListenerLmlAttribute implements LmlAttribute<DragPane> {
    @Override
    public Class<DragPane> getHandledType() {
        return DragPane.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final DragPane actor, final String rawAttributeData) {
        @SuppressWarnings("unchecked") final ActorConsumer<Boolean, Actor> listener = (ActorConsumer<Boolean, Actor>) parser
                .parseAction(rawAttributeData, new Actor());
        if (listener == null) {
            parser.throwErrorIfStrict(
                    "Drag pane listener attribute expects ID of an action referencing method that consumes an Actor and returns boolean/Boolean. No action found for data: "
                            + rawAttributeData);
            return;
        }
        actor.setListener(new DragPaneListener() {
            @Override
            public boolean accept(final DragPane dragPane, final Actor actor) {
                return listener.consume(actor);
            }
        });
    }
}
