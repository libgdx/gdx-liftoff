package com.github.czyzby.lml.parser.impl.attribute.input;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link TextField#setTextFieldFilter(TextFieldFilter)}. Expects an action ID that references a method which
 * consumes a {@link Character} and returns boolean (boxed or unboxed). Invoked each time a character is typed into the
 * text field. If the method returns true, character will be accepted and appended to the text field. If false,
 * character will be ignored. Mapped to "filter", "textFilter", "textFieldFilter".
 *
 * @author MJ
 * @see TextFieldFilter */
public class TextFieldFilterLmlAttribute implements LmlAttribute<TextField> {
    @Override
    public Class<TextField> getHandledType() {
        return TextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final TextField actor,
            final String rawAttributeData) {
        @SuppressWarnings("unchecked") final ActorConsumer<Boolean, Character> filter = (ActorConsumer<Boolean, Character>) parser
                .parseAction(rawAttributeData, Character.valueOf(' '));
        if (filter == null) {
            parser.throwErrorIfStrict(
                    "Text field filter attribute requires ID of an action that consumes a Character and returns a boolean or Boolean. Valid action not found for name: "
                            + rawAttributeData);
            return;
        }
        actor.setTextFieldFilter(new TextFieldFilter() {
            @Override
            public boolean acceptChar(final TextField textField, final char character) {
                return filter.consume(character);
            }
        });
    }
}
