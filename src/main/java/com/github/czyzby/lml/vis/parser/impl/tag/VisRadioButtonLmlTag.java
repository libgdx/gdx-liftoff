package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisCheckBox.VisCheckBoxStyle;
import com.kotcrab.vis.ui.widget.VisRadioButton;

/** Handles {@link VisRadioButton} actors. Treats children as a check box parent. Mapped to "radioButton",
 * "visRadioButton".
 *
 * @author MJ
 * @see VisCheckBoxLmlTag */
public class VisRadioButtonLmlTag extends VisCheckBoxLmlTag {
    public VisRadioButtonLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TextLmlActorBuilder getNewInstanceOfBuilder() {
        final TextLmlActorBuilder builder = new TextLmlActorBuilder();
        builder.setStyleName("radio"); // Default radio button style name.
        return builder;
    }

    @Override
    protected TextButton getNewInstanceOfTextButton(final TextLmlActorBuilder builder) {
        return new VisRadioButton(builder.getText(),
                getSkin(builder).get(builder.getStyleName(), VisCheckBoxStyle.class));
    }
}
