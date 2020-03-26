package com.github.czyzby.lml.vis.parser.impl.tag;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/** Handles {@link VisValidatableTextField} actor. Appends plain text between tags to itself. Mapped to "validatable",
 * "validatableTextField", "visValidatableTextField".
 *
 * @author MJ */
public class VisValidatableTextFieldLmlTag extends VisTextFieldLmlTag {
    public VisValidatableTextFieldLmlTag(final LmlParser parser, final LmlTag parentTag,
            final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected VisTextField getNewInstanceOfTextField(final TextLmlActorBuilder textBuilder) {
        return new VisValidatableTextField(textBuilder.getText(), textBuilder.getStyleName());
    }
}
