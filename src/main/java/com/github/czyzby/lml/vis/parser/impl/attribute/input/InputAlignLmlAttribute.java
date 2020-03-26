package com.github.czyzby.lml.vis.parser.impl.attribute.input;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.widget.VisTextField;

/** See {@link VisTextField#setAlignment(int)}. Expects a string matching
 * {@link com.github.czyzby.kiwi.util.gdx.scene2d.Alignment} enum constant name. Mapped to "textAlign", "inputAlign",
 * "textAlignment".
 *
 * @author MJ */
public class InputAlignLmlAttribute implements LmlAttribute<VisTextField> {
    @Override
    public Class<VisTextField> getHandledType() {
        return VisTextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisTextField actor,
            final String rawAttributeData) {
        actor.setAlignment(LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
    }
}
