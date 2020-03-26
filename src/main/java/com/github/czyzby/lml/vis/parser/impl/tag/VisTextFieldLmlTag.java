package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractNonParentalActorLmlTag;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.widget.VisTextField;

/** Handles {@link VisTextField} actor. Appends plain text between tags to itself. Mapped to "textField",
 * "visTextField".
 *
 * @author MJ */
public class VisTextFieldLmlTag extends AbstractNonParentalActorLmlTag {
    public VisTextFieldLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TextLmlActorBuilder getNewInstanceOfBuilder() {
        return new TextLmlActorBuilder();
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        final TextLmlActorBuilder textBuilder = (TextLmlActorBuilder) builder;
        return getNewInstanceOfTextField(textBuilder);
    }

    /** @param textBuilder contains initial text data and style.
     * @return a new instance of {@link VisTextField}. */
    protected VisTextField getNewInstanceOfTextField(final TextLmlActorBuilder textBuilder) {
        return new VisTextField(textBuilder.getText(), textBuilder.getStyleName());
    }

    /** @return casted actor. */
    protected VisTextField getTextField() {
        return (VisTextField) getActor();
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        final VisTextField textField = getTextField();
        final String textToAppend = getParser().parseString(plainTextLine, getActor());
        if (Strings.isEmpty(textField.getText())) {
            textField.setText(textToAppend);
        } else {
            if (LmlUtilities.isMultiline(textField)) {
                textField.appendText('\n' + textToAppend);
            } else {
                textField.appendText(textToAppend);
            }
        }
    }
}
