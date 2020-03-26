package com.github.czyzby.lml.vis.parser.impl.tag;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.widget.ScrollableTextArea;
import com.kotcrab.vis.ui.widget.VisTextField;

/**
 * Manages {@link ScrollableTextArea} widgets. Converts text between tags into text area content. Compatible with
 * scroll pane widgets. Mapped to "scrollableTextArea"
 * @author MJ
 */
public class ScrollableTextAreaLmlTag extends VisTextAreaLmlTag {
    public ScrollableTextAreaLmlTag(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected VisTextField getNewInstanceOfTextField(TextLmlActorBuilder textBuilder) {
        final ScrollableTextArea textArea = new ScrollableTextArea(textBuilder.getText(), textBuilder.getStyleName());
        LmlUtilities.getLmlUserObject(textArea).setData(Boolean.TRUE); // Setting as multiline by default.
        return textArea;
    }
}
