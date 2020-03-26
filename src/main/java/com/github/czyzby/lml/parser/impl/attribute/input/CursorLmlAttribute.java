package com.github.czyzby.lml.parser.impl.attribute.input;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** See {@link TextField#setCursorPosition(int)}. Cursor position will be set after the actor is fully constructed and
 * its tag is closed, so if you keep text field data between its tags, cursor position setting will still work. Mapped
 * to "cursor", "cursorPosition".
 *
 * @author MJ */
public class CursorLmlAttribute implements LmlAttribute<TextField> {
    @Override
    public Class<TextField> getHandledType() {
        return TextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final TextField actor,
            final String rawAttributeData) {
        LmlUtilities.getLmlUserObject(actor).addOnCloseAction(new ActorConsumer<Object, Object>() {
            @Override
            public Void consume(final Object widget) {
                actor.setCursorPosition(parser.parseInt(rawAttributeData, actor));
                return null;
            }
        });
    }
}
