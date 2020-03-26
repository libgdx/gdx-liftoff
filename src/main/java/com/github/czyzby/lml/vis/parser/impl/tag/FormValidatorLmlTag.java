package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.github.czyzby.lml.vis.ui.VisFormTable;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator;

/** Maintains a {@link SimpleFormValidator} through a specialized table widget: {@link VisFormTable}. As a parent, works
 * similarly to a table, although it adds all {@link com.kotcrab.vis.ui.widget.VisValidatableTextField} children to the
 * internally managed form. Mapped to "form", "formValidator", "formTable".
 *
 * @author MJ */
public class FormValidatorLmlTag extends VisTableLmlTag {
    public FormValidatorLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new VisFormTable();
    }

    @Override
    protected VisFormTable getTable() {
        return (VisFormTable) super.getTable();
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        final Actor child = childTag.getActor();
        if (LmlUtilities.getLmlUserObject(child).getCell() == null) {
            // Adds child to the table. Handles searching.
            addChild(child);
        } else {
            // Actor was previously added to the cell before his tag was closed and this method was called. This is
            // expected if the actor had any cell attributes - to change cell settings, the actor _needs_ to be in a
            // table. This means that VisFormTable had this actor before it parsed its children.
            if (child instanceof Group) {
                // Finding all VisValidatableTextFields recursively:
                getTable().findValidatables((Group) child);
            }
        }
    }
}
