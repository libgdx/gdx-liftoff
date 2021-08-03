package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextArea;

/** Handles {@link VisTextArea} actor. Text areas are set as multiline by default. Appends plain text between tags to
 * itself. Mapped to "textArea", "visTextArea".
 *
 * @author MJ */
public class BasicVisTextAreaLmlTag extends BasicVisTextFieldLmlTag {
    public BasicVisTextAreaLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TextField getNewInstanceOfTextField(final TextLmlActorBuilder textBuilder) {
        final TextArea textArea = new TextArea(textBuilder.getText(), VisUI.getSkin(), textBuilder.getStyleName());
//        LmlUtilities.getLmlUserObject(textArea).setData(Boolean.TRUE); // Setting as multiline by default.
        return textArea;
    }
}
