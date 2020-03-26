package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.LinkLabel;

/** Handles {@link LinkLabel} actor. Mapped to "linkLabel", "link".
 *
 * @author Kotcrab */
public class LinkLabelLmlTag extends VisLabelLmlTag {
    public LinkLabelLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        // Safe to cast, see super#getNewInstanceOfBuilder():
        final TextLmlActorBuilder textBuilder = (TextLmlActorBuilder) builder;
        return new LinkLabel(textBuilder.getText(), Strings.EMPTY_STRING, builder.getStyleName());
    }
}
