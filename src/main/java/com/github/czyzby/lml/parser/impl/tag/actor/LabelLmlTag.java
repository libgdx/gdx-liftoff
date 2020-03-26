package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractNonParentalActorLmlTag;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Handles {@link Label} actor. Mapped to "label". Note that most parental tags will add their plain text between tags
 * as labels with default style.
 *
 * @author MJ */
public class LabelLmlTag extends AbstractNonParentalActorLmlTag {
    public LabelLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TextLmlActorBuilder getNewInstanceOfBuilder() {
        return new TextLmlActorBuilder();
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        final TextLmlActorBuilder textBuilder = (TextLmlActorBuilder) builder;
        return new Label(textBuilder.getText(), getSkin(builder), textBuilder.getStyleName());
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        final Label label = getLabel();
        final String textToAppend = getParser().parseString(plainTextLine, label);
        if (Strings.isEmpty(label.getText())) {
            // Label is currently empty, so we just set the text as initial value.
            label.setText(textToAppend);
        } else {
            if (LmlUtilities.isMultiline(label)) {
                // Label is multiline. We might want to append an extra new line char.
                label.getText().append('\n');
            }
            label.getText().append(textToAppend);
        }
        label.invalidate();
    }

    /** @return casted actor reference. */
    protected Label getLabel() {
        return (Label) getActor();
    }
}
