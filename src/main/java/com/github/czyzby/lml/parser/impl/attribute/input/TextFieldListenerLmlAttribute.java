package com.github.czyzby.lml.parser.impl.attribute.input;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link TextField#setTextFieldListener(TextFieldListener)}. Expects an action ID that references a method that
 * consumes a {@link Character}. Invoked each time a character is typed into the text field. Mapped to "listener",
 * "textListener", "textFieldListener".
 *
 * @author MJ */
public class TextFieldListenerLmlAttribute implements LmlAttribute<TextField> {
    @Override
    public Class<TextField> getHandledType() {
        return TextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final TextField actor,
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
            public void keyTyped(final TextField textField, final char character) {
                listener.consume(character);
            }
        });
    }
}
