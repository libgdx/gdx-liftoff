package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Handles {@code CheckBox} actor. Allows its children tags to use cell attributes. As opposed to a table, it appends
 * plain text data to its label rather than create and add a new label for each line. Mapped to "checkBox".
 *
 * @author MJ */
public class CheckBoxLmlTag extends TextButtonLmlTag {
    public CheckBoxLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected CheckBox getNewInstanceOfTextButton(final TextLmlActorBuilder builder) {
        return new CheckBox(builder.getText(), getSkin(builder), builder.getStyleName());
    }

    @Override
    protected boolean hasComponentActors() {
        return true;
    }

    @Override
    protected Actor[] getComponentActors(final Actor actor) {
        final CheckBox checkBox = (CheckBox) actor;
        return new Actor[] { checkBox.getLabel(), checkBox.getImage() };
    }
}
