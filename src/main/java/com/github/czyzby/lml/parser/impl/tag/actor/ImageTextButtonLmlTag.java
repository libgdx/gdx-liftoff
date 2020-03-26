package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Handles {@link ImageTextButton} actor. Treats children as a button parent tag. As opposed to a table, it appends
 * plain text data to its label rather than creating and adding a new label for each line. Mapped to "imageTextButton".
 *
 * @author MJ */
public class ImageTextButtonLmlTag extends ButtonLmlTag {
    public ImageTextButtonLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TextLmlActorBuilder getNewInstanceOfBuilder() {
        return new TextLmlActorBuilder();
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        final TextLmlActorBuilder textBuilder = (TextLmlActorBuilder) builder;
        return new ImageTextButton(textBuilder.getText(), getSkin(builder), builder.getStyleName());
    }

    @Override
    protected boolean hasComponentActors() {
        return true;
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        final ImageTextButton button = (ImageTextButton) getActor();
        final String textToAppend = getParser().parseString(plainTextLine, getActor());
        if (Strings.isEmpty(button.getText())) {
            button.setText(textToAppend);
        } else {
            if (LmlUtilities.isMultiline(button)) {
                button.setText(button.getText().toString() + '\n' + textToAppend);
            } else {
                button.setText(button.getText().toString() + textToAppend);
            }
        }
    }

    @Override
    protected Actor[] getComponentActors(final Actor actor) {
        final ImageTextButton button = (ImageTextButton) actor;
        return new Actor[] { button.getImage(), button.getLabel() };
    }
}
