package com.github.czyzby.lml.vis.parser.impl.attribute.picker.basic;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.color.BasicColorPicker;

/** See {@link BasicColorPicker#setShowHexFields(boolean)}. Mapped to "showHex", "showHexField".
 *
 * @author MJ */
public class ShowHexFieldLmlAttribute implements LmlAttribute<BasicColorPicker> {
    @Override
    public Class<BasicColorPicker> getHandledType() {
        return BasicColorPicker.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final BasicColorPicker actor,
            final String rawAttributeData) {
        actor.setShowHexFields(parser.parseBoolean(rawAttributeData, actor));
    }
}
