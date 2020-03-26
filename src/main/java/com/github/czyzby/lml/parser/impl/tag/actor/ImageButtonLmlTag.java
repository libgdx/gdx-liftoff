package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Handles {@link ImageButton} actor. Treats children as a {@link ButtonLmlTag}. Mapped to "imageButton".
 *
 * @author MJ */
public class ImageButtonLmlTag extends ButtonLmlTag {
    public ImageButtonLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        final Skin skin = getSkin(builder);
        final ImageButton button = new ImageButton(skin, builder.getStyleName());
        button.setSkin(skin); // Not set internally by constructor.
        return button;
    }

    @Override
    protected boolean hasComponentActors() {
        return true;
    }

    @Override
    protected Actor[] getComponentActors(final Actor actor) {
        return new Actor[] { ((ImageButton) actor).getImage() };
    }
}
