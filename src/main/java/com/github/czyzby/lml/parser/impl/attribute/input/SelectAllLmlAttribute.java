package com.github.czyzby.lml.parser.impl.attribute.input;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Expects a boolean. If true, will eventually invoke {@link TextField#selectAll()} after the widget is fully
 * initiated. Mapped to "selectAll".
 *
 * @author MJ */
public class SelectAllLmlAttribute implements LmlAttribute<TextField> {
    @Override
    public Class<TextField> getHandledType() {
        return TextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final TextField actor,
            final String rawAttributeData) {
        if (parser.parseBoolean(rawAttributeData, actor)) {
            LmlUtilities.getLmlUserObject(actor).addOnCloseAction(new ActorConsumer<Object, Object>() {
                @Override
                public Object consume(final Object widget) {
                    actor.selectAll();
                    return null;
                }
            });
        }
    }
}
