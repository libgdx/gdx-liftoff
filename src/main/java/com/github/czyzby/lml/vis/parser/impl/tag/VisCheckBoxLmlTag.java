package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisCheckBox;

/** Handles {@code VisCheckBox} actor. Allows its children tags to use cell attributes. As opposed to a table, it
 * appends plain text data to its label rather than create and add a new label for each line. Mapped to "checkBox",
 * "visCheckBox".
 *
 * @author MJ */
public class VisCheckBoxLmlTag extends VisTextButtonLmlTag {
    public VisCheckBoxLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TextButton getNewInstanceOfTextButton(final TextLmlActorBuilder builder) {
        return new VisCheckBox(builder.getText(), builder.getStyleName());
    }

    @Override
    protected boolean hasComponentActors() {
        return true;
    }

    @Override
    protected Actor[] getComponentActors(final Actor actor) {
        final VisCheckBox checkBox = (VisCheckBox) actor;
        return new Actor[] { checkBox.getLabel(), checkBox.getBackgroundImage(), checkBox.getTickImage() };
    }
}
