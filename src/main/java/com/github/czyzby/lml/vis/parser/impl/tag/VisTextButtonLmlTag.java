package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.actor.TextButtonLmlTag;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisTextButton;

/** Handles {@link VisTextButton} actors. Appends children as {@link Table}. Converts plain text between tags into its
 * label text. Mapped to "textButton", "visTextButton".
 *
 * @author MJ */
public class VisTextButtonLmlTag extends TextButtonLmlTag {
    public VisTextButtonLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TextButton getNewInstanceOfTextButton(final TextLmlActorBuilder builder) {
        return new VisTextButton(builder.getText(), builder.getStyleName());
    }
}
