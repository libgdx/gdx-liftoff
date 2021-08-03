package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.setup.views.widgets.ScrollableTextArea;

/**
 * Manages {@link ScrollableTextArea} widgets. Converts text between tags into text area content. Compatible with
 * scroll pane widgets. Mapped to "scrollableTextArea"
 * @author MJ
 */
public class BasicScrollableTextAreaLmlTag extends BasicVisTextAreaLmlTag {
    public BasicScrollableTextAreaLmlTag(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TextField getNewInstanceOfTextField(TextLmlActorBuilder textBuilder) {
        final ScrollableTextArea textArea = new ScrollableTextArea(textBuilder.getText(), textBuilder.getStyleName());
//        LmlUtilities.getLmlUserObject(textArea).setData(Boolean.TRUE); // Setting as multiline by default.
        return textArea;
    }
}
