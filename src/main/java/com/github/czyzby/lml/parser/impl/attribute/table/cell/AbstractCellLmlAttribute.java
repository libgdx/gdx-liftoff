package com.github.czyzby.lml.parser.impl.attribute.table.cell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Abstract base for all attributes that can be applied to any actor, provided that its direct parent is a table.
 *
 * @author MJ */
public abstract class AbstractCellLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public final void process(final LmlParser parser, final LmlTag tag, final Actor actor,
            final String rawAttributeData) {
        if (tag.isAttachable()) {
            parser.throwErrorIfStrict(tag.getTagName()
                    + " is an attachable tag and cannot be stored in a table, even if its direct parent is a table tag. Attachable actors, like tooltips, are usually autonomic: they cannot be added to a table or honor cell settings.");
            return;
        }
        final Cell<?> cell = LmlUtilities.getCell(actor, tag.getParent());
        if (cell == null) {
            processForActor(parser, tag, actor, rawAttributeData);
        } else {
            process(parser, tag, actor, cell, rawAttributeData);
        }
    }

    /** @param parser handles LML template parsing.
     * @param tag contains raw tag data. Allows to access actor's parent.
     * @param actor handled actor instance, casted for convenience.
     * @param cell non-null cell storing the actor.
     * @param rawAttributeData unparsed LML attribute data that should be handled by this attribute processor. Common
     *            data types (string, int, float, boolean, action) are already handled by LML parser implementation, so
     *            make sure to invoke its methods. */
    public abstract void process(final LmlParser parser, final LmlTag tag, final Actor actor, final Cell<?> cell,
            final String rawAttributeData);

    /** This method is called if the actor is not in a cell. It should set the property in the actor itself, provided
     * that its correct and can be applied. By default, throws an exception if the parser is strict.
     *
     * @param parser handles LML template parsing.
     * @param tag contains raw tag data. Allows to access actor's parent.
     * @param actor handled actor instance, casted for convenience.
     * @param rawAttributeData unparsed LML attribute data that should be handled by this attribute processor. Common
     *            data types (string, int, float, boolean, action) are already handled by LML parser implementation, so
     *            make sure to invoke its methods. */
    protected void processForActor(final LmlParser parser, final LmlTag tag, final Actor actor,
            final String rawAttributeData) {
        parser.throwErrorIfStrict("\"" + tag.getTagName()
                + "\" tag has a table cell attribute, but is not directly in a table. Cannot set table cell attribute value with raw data: "
                + rawAttributeData + " with attribute processor: " + this);
    }
}
