
package com.github.czyzby.lml.vis.parser.impl.attribute.button;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.kotcrab.vis.ui.widget.VisImageTextButton.VisImageTextButtonStyle;

/** Allows to force image of a {@link VisImageTextButton}. This attribute will copy button's style and change
 * {@link VisImageTextButtonStyle#imageUp} - if this is the only image in the style, it will be
 * always drawn on the button. Mapped to "image", "icon".
 *
 * @author MJ */
public class TextButtonImageLmlAttribute implements LmlAttribute<VisImageTextButton> {
    @Override
    public Class<VisImageTextButton> getHandledType() {
        return VisImageTextButton.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisImageTextButton actor,
            final String rawAttributeData) {
        final VisImageTextButtonStyle style = new VisImageTextButtonStyle(actor.getStyle());
        style.imageUp = parser.getData().getDefaultSkin().getDrawable(parser.parseString(rawAttributeData, actor));
        actor.setStyle(style);
    }
}
