package com.github.czyzby.lml.parser.impl.attribute.input;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** See {@link TextField#setAlignment(int)}. Expects a string matching
 * {@link com.github.czyzby.kiwi.util.gdx.scene2d.Alignment} enum constant name. Mapped to "textAlign", "inputAlign",
 * "textAlignment".
 *
 * @author MJ */
public class InputAlignLmlAttribute implements LmlAttribute<TextField> {
    @Override
    public Class<TextField> getHandledType() {
        return TextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final TextField actor,
            final String rawAttributeData) {
        actor.setAlignment(LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
    }
}
