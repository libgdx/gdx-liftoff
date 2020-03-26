package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Handles {@link TextArea} actor. Text areas are set as multiline by default. Appends plain text between tags to
 * itself. Mapped to "textArea".
 *
 * @author MJ */
public class TextAreaLmlTag extends TextFieldLmlTag {
    public TextAreaLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TextField getNewInstanceOfTextField(final TextLmlActorBuilder textBuilder) {
        final TextArea textArea = new TextArea(textBuilder.getText(), getSkin(textBuilder), textBuilder.getStyleName());
        LmlUtilities.getLmlUserObject(textArea).setData(Boolean.TRUE); // Setting as multiline by default.
        return textArea;
    }
}
