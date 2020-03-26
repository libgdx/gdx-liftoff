package com.github.czyzby.lml.vis.parser.impl.attribute.input;

import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisTextField;

/** See {@link VisTextField#setPasswordCharacter(char)}. Expects a string with length of 1. Mapped to "passwordChar",
 * "passwordCharacter", "passChar", "passCharacter".
 *
 * @author MJ */
public class PasswordCharacterLmlAttribute implements LmlAttribute<VisTextField> {
    @Override
    public Class<VisTextField> getHandledType() {
        return VisTextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisTextField actor,
            final String rawAttributeData) {
        final String passwordCharacter = parser.parseString(rawAttributeData, actor);
        if (Strings.isEmpty(passwordCharacter)) {
            parser.throwError("Password character setting cannot be empty. String with length of 1 is required.");
        } else if (passwordCharacter.length() != 1) {
            parser.throwErrorIfStrict("String with length of 1 is required for password character setting.");
        }
        // At this point, string must have at least 1 character.
        actor.setPasswordCharacter(passwordCharacter.charAt(0));
    }
}
