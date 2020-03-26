package com.github.czyzby.lml.parser.impl.attribute.image.animated;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.AnimatedImage;
import com.github.czyzby.lml.util.LmlUtilities;

/** See {@link AnimatedImage#setCurrentFrame(int)}. Expects index of the chosen frame in the drawables array. Mapped to
 * "frame", "currentFrame".
 *
 * @author MJ */
public class CurrentFrameLmlAttribute implements LmlAttribute<AnimatedImage> {
    @Override
    public Class<AnimatedImage> getHandledType() {
        return AnimatedImage.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final AnimatedImage actor,
            final String rawAttributeData) {
        LmlUtilities.getLmlUserObject(actor).addOnCreateAction(new ActorConsumer<Void, Object>() {
            @Override
            public Void consume(final Object widget) {
                actor.setCurrentFrame(parser.parseInt(rawAttributeData, actor));
                return null;
            }
        });
    }
}
