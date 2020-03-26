package com.github.czyzby.lml.vis.parser.impl.tag;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.widget.VisTextArea;
import com.kotcrab.vis.ui.widget.VisTextField;

/** Handles {@link VisTextArea} actor. Text areas are set as multiline by default. Appends plain text between tags to
 * itself. Mapped to "textArea", "visTextArea".
 *
 * @author MJ */
public class VisTextAreaLmlTag extends VisTextFieldLmlTag {
    public VisTextAreaLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected VisTextField getNewInstanceOfTextField(final TextLmlActorBuilder textBuilder) {
        final VisTextArea textArea = new VisTextArea(textBuilder.getText(), textBuilder.getStyleName());
        LmlUtilities.getLmlUserObject(textArea).setData(Boolean.TRUE); // Setting as multiline by default.
        return textArea;
    }
}
