package com.github.czyzby.lml.parser.impl.attribute.image.animated;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.AnimatedImage;

/** See {@link AnimatedImage#setBouncing(boolean)}. Mapped to "bounce", "bouncing".
 *
 * @author MJ */
public class BouncingLmlAttribute implements LmlAttribute<AnimatedImage> {
    @Override
    public Class<AnimatedImage> getHandledType() {
        return AnimatedImage.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final AnimatedImage actor,
            final String rawAttributeData) {
        actor.setBouncing(parser.parseBoolean(rawAttributeData, actor));
    }
}
