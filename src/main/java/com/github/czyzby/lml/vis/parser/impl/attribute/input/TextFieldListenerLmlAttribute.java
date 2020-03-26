package com.github.czyzby.lml.vis.parser.impl.attribute.input;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldListener;

/** See {@link VisTextField#setTextFieldListener(TextFieldListener)}. Expects an action ID that references a method that
 * consumes a {@link Character}. Invoked each time a character is typed into the text field. Mapped to "listener",
 * "textListener", "textFieldListener".
 *
 * @author MJ */
public class TextFieldListenerLmlAttribute implements LmlAttribute<VisTextField> {
    @Override
    public Class<VisTextField> getHandledType() {
        return VisTextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisTextField actor,
            final String rawAttributeData) {
        final ActorConsumer<?, Character> listener = parser.parseAction(rawAttributeData, Character.valueOf(' '));
        if (listener == null) {
            parser.throwErrorIfStrict(
                    "Text field listener attribute requires ID of an action that consumes a Character. Valid action not found for name: "
                            + rawAttributeData);
            return;
        }
        actor.setTextFieldListener(new TextFieldListener() {
            @Override
            public void keyTyped(final VisTextField textField, final char character) {
                listener.consume(character);
            }
        });
    }
}
