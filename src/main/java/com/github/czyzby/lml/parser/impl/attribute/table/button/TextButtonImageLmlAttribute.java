
package com.github.czyzby.lml.parser.impl.attribute.table.button;

import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Allows to force image of a {@link ImageTextButton}. This attribute will copy button's style and change
 * {@link ImageTextButtonStyle#imageUp} - if this is the only image in the style, it will be always
 * drawn on the button. Mapped to "image", "icon".
 *
 * @author MJ */
public class TextButtonImageLmlAttribute implements LmlAttribute<ImageTextButton> {
    @Override
    public Class<ImageTextButton> getHandledType() {
        return ImageTextButton.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final ImageTextButton actor,
            final String rawAttributeData) {
        final ImageTextButtonStyle style = new ImageTextButtonStyle(actor.getStyle());
        style.imageUp = parser.getData().getDefaultSkin().getDrawable(parser.parseString(rawAttributeData, actor));
        actor.setStyle(style);
    }
}
