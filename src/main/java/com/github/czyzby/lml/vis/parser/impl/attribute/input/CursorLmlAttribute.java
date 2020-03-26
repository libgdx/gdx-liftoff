package com.github.czyzby.lml.vis.parser.impl.attribute.input;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.widget.VisTextField;

/** See {@link VisTextField#setCursorPosition(int)}. Cursor position will be set after the actor is fully constructed
 * and its tag is closed, so if you keep text field data between its tags, cursor position setting will still work.
 * Mapped to "cursor", "cursorPos", "cursorPosition".
 *
 * @author MJ */
public class CursorLmlAttribute implements LmlAttribute<VisTextField> {
    @Override
    public Class<VisTextField> getHandledType() {
        return VisTextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisTextField actor,
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
