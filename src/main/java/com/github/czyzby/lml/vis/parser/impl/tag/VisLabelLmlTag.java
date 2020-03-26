package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.actor.LabelLmlTag;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisLabel;

/** Handles {@link VisLabel} actors. Cannot have actor children. Appends plain text between tags to itself. Mapped to
 * "label", "visLabel".
 *
 * @author MJ */
public class VisLabelLmlTag extends LabelLmlTag {
    public VisLabelLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        // Safe to cast, see super#getNewInstanceOfBuilder():
        final TextLmlActorBuilder textBuilder = (TextLmlActorBuilder) builder;
        return new VisLabel(textBuilder.getText(), builder.getStyleName());
    }
}
