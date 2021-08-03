package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.setup.views.widgets.ScrollableTextArea;

/**
 * Manages {@link ScrollableTextArea} widgets. Converts text between tags into text area content. Compatible with
 * scroll pane widgets. Mapped to "scrollableTextArea"
 * @author MJ
 */
public class BasicScrollableTextAreaLmlTag extends VisLabelLmlTag {
    public BasicScrollableTextAreaLmlTag(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(LmlActorBuilder builder) {
        // Safe to cast, see super#getNewInstanceOfBuilder():
        final TextLmlActorBuilder textBuilder = (TextLmlActorBuilder) builder;
        return new ScrollableTextArea(textBuilder.getText(), builder.getStyleName());
    }
}
