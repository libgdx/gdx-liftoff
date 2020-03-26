package com.github.czyzby.lml.parser.impl.attribute.label;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** See {@link Label#setAlignment(int)}. Expects a string matching
 * {@link com.github.czyzby.kiwi.util.gdx.scene2d.Alignment} enum constant name. Mapped to "textAlign", "textAlignment".
 *
 * @author MJ */
public class TextAlignmentLmlAttribute implements LmlAttribute<Label> {
    @Override
    public Class<Label> getHandledType() {
        return Label.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Label actor, final String rawAttributeData) {
        actor.setAlignment(LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
    }
}
