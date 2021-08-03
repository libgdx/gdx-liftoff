package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.ui.alt.widget.VisValidatableTextField;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextField;

/** Handles {@link VisValidatableTextField} actor. Appends plain text between tags to itself. Mapped to "validatable",
 * "validatableTextField", "visValidatableTextField".
 *
 * @author MJ */
public class BasicVisValidatableTextFieldLmlTag extends BasicVisTextFieldLmlTag {
    public BasicVisValidatableTextFieldLmlTag(final LmlParser parser, final LmlTag parentTag,
                                              final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TextField getNewInstanceOfTextField(final TextLmlActorBuilder textBuilder) {
        return new VisValidatableTextField(textBuilder.getText(), textBuilder.getStyleName());
    }
}
