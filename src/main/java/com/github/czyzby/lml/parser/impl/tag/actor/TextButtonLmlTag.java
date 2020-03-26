package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Handles {@link TextButton} actor. Allows its children tags to use cell attributes. As opposed to a table, it appends
 * plain text data to its label rather than create and add a new label for each line. Mapped to "textButton".
 *
 * @author MJ */
public class TextButtonLmlTag extends ButtonLmlTag {
    public TextButtonLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TextLmlActorBuilder getNewInstanceOfBuilder() {
        return new TextLmlActorBuilder();
    }

    @Override
    protected final Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return getNewInstanceOfTextButton((TextLmlActorBuilder) builder);
    }

    /** @param builder contains data necessary to construct a text button.
     * @return a new instance of TextButton. */
    protected TextButton getNewInstanceOfTextButton(final TextLmlActorBuilder builder) {
        return new TextButton(builder.getText(), getSkin(builder), builder.getStyleName());
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        final TextButton button = getTextButton();
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

    /** @return casted actor. */
    protected TextButton getTextButton() {
        return (TextButton) getActor();
    }

    @Override
    protected boolean hasComponentActors() {
        return true;
    }

    @Override
    protected Actor[] getComponentActors(final Actor actor) {
        return new Actor[] { ((TextButton) actor).getLabel() };
    }
}
