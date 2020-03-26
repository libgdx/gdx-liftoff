package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.actor.ButtonLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;

/** Handles {@link VisImageButton} actor. Treats children as a {@link ButtonLmlTag}. Mapped to "imageButton",
 * "visImageButton".
 *
 * @author MJ */
public class VisImageButtonLmlTag extends ButtonLmlTag {
    public VisImageButtonLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        final Skin skin = getSkin(builder);
        final VisImageButton button = new VisImageButton(skin.get(builder.getStyleName(), VisImageButtonStyle.class));
        button.setSkin(skin); // Not set internally by the constructor.
        return button;
    }

    @Override
    protected boolean hasComponentActors() {
        return true;
    }

    @Override
    protected Actor[] getComponentActors(final Actor actor) {
        return new Actor[] { ((VisImageButton) actor).getImage() };
    }
}
