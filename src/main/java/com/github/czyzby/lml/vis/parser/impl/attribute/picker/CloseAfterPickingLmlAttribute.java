package com.github.czyzby.lml.vis.parser.impl.attribute.picker;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.color.ColorPicker;

/** See {@link ColorPicker#setCloseAfterPickingFinished(boolean)}. Mapped to "closeAfterPickingFinished", "closeAfter".
 *
 * @author MJ */
public class CloseAfterPickingLmlAttribute implements LmlAttribute<ColorPicker> {
    @Override
    public Class<ColorPicker> getHandledType() {
        return ColorPicker.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final ColorPicker actor,
            final String rawAttributeData) {
        actor.setCloseAfterPickingFinished(parser.parseBoolean(rawAttributeData, actor));
    }
}
