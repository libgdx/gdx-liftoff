
package com.github.czyzby.lml.vis.parser.impl.attribute.button;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;

/** Allows to force image of a {@link VisImageButton}. This attribute will copy button's style and change
 * {@link VisImageButtonStyle#imageUp} - if this is the only image in the style, it will be always drawn
 * on the button. Expects a string - name of a drawable in the default skin. Mapped to "image", "icon".
 *
 * @author MJ */
public class ButtonImageLmlAttribute implements LmlAttribute<VisImageButton> {
    @Override
    public Class<VisImageButton> getHandledType() {
        return VisImageButton.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisImageButton actor,
            final String rawAttributeData) {
        final VisImageButtonStyle style = new VisImageButtonStyle(actor.getStyle());
        style.imageUp = parser.getData().getDefaultSkin().getDrawable(parser.parseString(rawAttributeData, actor));
        actor.setStyle(style);
    }
}
