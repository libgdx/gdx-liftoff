package com.github.czyzby.lml.vis.parser.impl.attribute.input;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisTextArea;

/** See {@link VisTextArea#setPrefRows(float)}. Mapped to "prefRows", "prefRowsAmount".
 *
 * @author MJ */
public class PrefRowsLmlAttribute implements LmlAttribute<VisTextArea> {
    @Override
    public Class<VisTextArea> getHandledType() {
        return VisTextArea.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisTextArea actor,
            final String rawAttributeData) {
        actor.setPrefRows(parser.parseFloat(rawAttributeData, actor));
    }
}
