package com.github.czyzby.lml.parser.impl.attribute.image;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** See {@link Image#setAlign(int)}. Expects a string matching (equal or equal ignoring case) enum constant of
 * {@link com.github.czyzby.kiwi.util.gdx.scene2d.Alignment}. Mapped to "imageAlign".
 *
 * @author MJ */
public class ImageAlignmentLmlAttribute implements LmlAttribute<Image> {
    @Override
    public Class<Image> getHandledType() {
        return Image.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Image actor, final String rawAttributeData) {
        actor.setAlign(LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
    }
}
