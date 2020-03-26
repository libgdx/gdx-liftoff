package com.github.czyzby.lml.vis.parser.impl.attribute.input;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisTextField;

/** See {@link VisTextField#setReadOnly(boolean)}. Mapped to "readOnly".
 *
 * @author MJ */
public class ReadOnlyLmlAttribute implements LmlAttribute<VisTextField> {
    @Override
    public Class<VisTextField> getHandledType() {
        return VisTextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisTextField actor,
            final String rawAttributeData) {
        actor.setReadOnly(parser.parseBoolean(rawAttributeData, actor));
    }
}
