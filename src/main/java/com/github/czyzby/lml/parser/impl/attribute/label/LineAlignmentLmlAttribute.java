package com.github.czyzby.lml.parser.impl.attribute.label;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** See {@link Label#setAlignment(int, int)}. Expects a string matching
 * {@link com.github.czyzby.kiwi.util.gdx.scene2d.Alignment} enum constant name. This attribute sets only second
 * argument (line align). Mapped to "lineAlign", "lineAlignment".
 *
 * @author MJ */
public class LineAlignmentLmlAttribute implements LmlAttribute<Label> {
    @Override
    public Class<Label> getHandledType() {
        return Label.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Label actor, final String rawAttributeData) {
        actor.setAlignment(actor.getLabelAlign(), LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
    }
}
