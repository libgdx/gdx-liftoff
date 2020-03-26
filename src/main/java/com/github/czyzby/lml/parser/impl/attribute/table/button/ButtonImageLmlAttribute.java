
package com.github.czyzby.lml.parser.impl.attribute.table.button;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Allows to force image of a {@link ImageButton}. This attribute will copy button's style and change
 * {@link ImageButtonStyle#imageUp} - if this is the only image in the style, it will be always drawn on the
 * button. Mapped to "image", "icon".
 *
 * @author MJ */
public class ButtonImageLmlAttribute implements LmlAttribute<ImageButton> {
    @Override
    public Class<ImageButton> getHandledType() {
        return ImageButton.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final ImageButton actor,
            final String rawAttributeData) {
        final ImageButtonStyle style = new ImageButtonStyle(actor.getStyle());
        style.imageUp = parser.getData().getDefaultSkin().getDrawable(parser.parseString(rawAttributeData, actor));
        actor.setStyle(style);
    }
}
