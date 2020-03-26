package com.github.czyzby.lml.parser.impl.attribute.input;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link TextField#setPasswordCharacter(char)}. Expects a string with length of 1. Mapped to "passwordChar",
 * "passwordCharacter".
 *
 * @author MJ */
public class PasswordCharacterLmlAttribute implements LmlAttribute<TextField> {
    @Override
    public Class<TextField> getHandledType() {
        return TextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final TextField actor,
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
