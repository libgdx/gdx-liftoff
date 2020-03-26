package com.github.czyzby.lml.vis.parser.impl.tag;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.util.highlight.BaseHighlighter;
import com.kotcrab.vis.ui.widget.HighlightTextArea;
import com.kotcrab.vis.ui.widget.ScrollableTextArea;
import com.kotcrab.vis.ui.widget.VisTextField;

/**
 * Manages {@link HighlightTextArea} widgets. Converts text between tags into text area content. Compatible with
 * scroll pane widgets. Mapped to "highlightTextArea"
 * @author MJ
 */
public class HighlightTextAreaLmlTag extends ScrollableTextAreaLmlTag {
    public HighlightTextAreaLmlTag(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected VisTextField getNewInstanceOfTextField(TextLmlActorBuilder textBuilder) {
        final HighlightTextArea textArea = new HighlightTextArea(textBuilder.getText(), textBuilder.getStyleName());
        LmlUtilities.getLmlUserObject(textArea).setData(Boolean.TRUE); // Setting as multiline by default.
        return textArea;
    }
}
