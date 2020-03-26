package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.AnimatedImage;

/** Handles {@link AnimatedImage} actor. Cannot have children. Expects an array (following LML syntax standards) of
 * names of drawables stored in the chosen skin as the style attribute. If style attribute is not set, image will
 * contain no frames and work as an empty {@link com.badlogic.gdx.scenes.scene2d.ui.Image} Mapped to "animatedImage".
 *
 * @author MJ */
public class AnimatedImageLmlTag extends ImageLmlTag {
    public AnimatedImageLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected LmlActorBuilder getNewInstanceOfBuilder() {
        final LmlActorBuilder builder = super.getNewInstanceOfBuilder();
        builder.setStyleName(Strings.EMPTY_STRING);
        return builder;
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        final String[] frames = getParser().parseArray(builder.getStyleName());
        return new AnimatedImage(getSkin(builder), frames);
    }
}
