package com.github.czyzby.lml.parser.impl.attribute.input;

import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link TextArea#setPrefRows(float)}. Mapped to "prefRows", "prefRowsAmount".
 *
 * @author MJ */
public class PrefRowsLmlAttribute implements LmlAttribute<TextArea> {
    @Override
    public Class<TextArea> getHandledType() {
        return TextArea.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final TextArea actor, final String rawAttributeData) {
        actor.setPrefRows(parser.parseFloat(rawAttributeData, actor));
    }
}
