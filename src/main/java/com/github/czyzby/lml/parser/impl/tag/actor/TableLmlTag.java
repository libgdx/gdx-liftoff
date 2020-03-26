package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Handles {@link Table} actor. Allows its children tags to use cell attributes. Adds plain text as labels created with
 * the same skin that was used to construct the table. Mapped to "table".
 *
 * @author MJ */
public class TableLmlTag extends AbstractActorLmlTag {
    public TableLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new Table(getSkin(builder));
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        addChild(childTag.getActor());
    }

    /** @param actor will be appended to the table. */
    protected void addChild(final Actor actor) {
        // Will assign the actor to the table if not added yet.
        LmlUtilities.getCell(actor, getTable());
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        final Table table = getTable();
        table.add(getParser().parseString(plainTextLine, getActor()));
        if (LmlUtilities.isOneColumn(table)) {
            table.row();
        }
    }

    /** @return casted actor. */
    protected Table getTable() {
        return (Table) getActor();
    }
}
