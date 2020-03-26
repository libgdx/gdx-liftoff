package com.github.czyzby.lml.vis.parser.impl.attribute.input;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter;

/** See {@link VisTextField#setTextFieldFilter(TextFieldFilter)}. Expects an action ID that references a method which
 * consumes a {@link Character} and returns boolean (boxed or unboxed). Invoked each time a character is typed into the
 * text field. If the method returns true, character will be accepted and appended to the text field. If false,
 * character will be ignored. Mapped to "filter", "textFilter", "textFieldFilter".
 *
 * @author MJ
 * @see TextFieldFilter */
public class TextFieldFilterLmlAttribute implements LmlAttribute<VisTextField> {
    @Override
    public Class<VisTextField> getHandledType() {
        return VisTextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisTextField actor,
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
            public boolean acceptChar(final VisTextField textField, final char character) {
                return filter.consume(character);
            }
        });
    }
}
