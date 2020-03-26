package com.github.czyzby.lml.parser.impl.attribute.layout;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** See {@link Layout#pack()}. Expects a boolean. If the value is true, the actor will be packed after all its children
 * are added. Mapped to "pack".
 *
 * @author MJ */
public class PackLmlAttribute extends AbstractLayoutLmlAttribute {
    @Override
    protected void process(final LmlParser parser, final LmlTag tag, final Layout layout, final Actor actor,
            final String rawAttributeData) {
        if (!parser.parseBoolean(rawAttributeData, actor)) {
            return;
        }
        LmlUtilities.getLmlUserObject(actor).addOnCloseAction(new ActorConsumer<Void, Object>() {
            @Override
            public Void consume(final Object actor) {
                layout.pack();
                return null;
            }
        });
    }
}
