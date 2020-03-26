package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractNonParentalActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Handles {@link Touchpad} actor. Cannot handle children tags or data between tags. Mapped to "touchpad".
 *
 * @author MJ */
public class TouchpadLmlTag extends AbstractNonParentalActorLmlTag {
    public TouchpadLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        // Setting dead zone radius to 0f. Should not throw exception, can be changed later with attributes.
        return new Touchpad(0f, getSkin(builder), builder.getStyleName());
    }
}
