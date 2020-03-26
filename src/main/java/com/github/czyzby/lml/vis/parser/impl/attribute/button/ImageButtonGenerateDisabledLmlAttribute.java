package com.github.czyzby.lml.vis.parser.impl.attribute.button;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisImageButton;

/** See {@link VisImageButton#setGenerateDisabledImage(boolean)}. Mapped to "generateDisabled", "generateDisabledImage".
 *
 * @author MJ */
public class ImageButtonGenerateDisabledLmlAttribute implements LmlAttribute<VisImageButton> {
    @Override
    public Class<VisImageButton> getHandledType() {
        return VisImageButton.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisImageButton actor,
            final String rawAttributeData) {
        actor.setGenerateDisabledImage(parser.parseBoolean(rawAttributeData, actor));
    }
}
