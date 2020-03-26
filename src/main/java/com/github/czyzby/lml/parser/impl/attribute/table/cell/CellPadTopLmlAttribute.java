package com.github.czyzby.lml.parser.impl.attribute.table.cell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** See {@link Cell#padTop(Value)}. See {@link LmlUtilities#parseHorizontalValue(LmlParser, LmlTag, Actor, String)} and
 * {@link LmlUtilities#parseVerticalValue(LmlParser, LmlTag, Actor, String)} for more info on value parsing. Mapped to
 * "padTop".
 *
 * @author MJ */
public class CellPadTopLmlAttribute extends AbstractCellLmlAttribute {
    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final Cell<?> cell,
            final String rawAttributeData) {
        final Value verticalValue = LmlUtilities.parseVerticalValue(parser, tag.getParent(), actor, rawAttributeData);
        cell.padTop(verticalValue);
    }

    @Override
    protected void processForActor(final LmlParser parser, final LmlTag tag, final Actor actor,
            final String rawAttributeData) {
        // Parsed if actor is not in a cell:
        if (actor instanceof Table) {
            final Value verticalValue = LmlUtilities.parseVerticalValue(parser, tag.getParent(), actor,
                    rawAttributeData);
            ((Table) actor).padTop(verticalValue);
        } else if (actor instanceof VerticalGroup) {
            ((VerticalGroup) actor).padTop(parser.parseFloat(rawAttributeData, actor));
        } else if (actor instanceof HorizontalGroup) {
            ((HorizontalGroup) actor).padTop(parser.parseFloat(rawAttributeData, actor));
        } else if (actor instanceof Container<?>) {
            ((Container<?>) actor)
                    .padTop(LmlUtilities.parseVerticalValue(parser, tag.getParent(), actor, rawAttributeData));
        } else {
            // Exception:
            super.processForActor(parser, tag, actor, rawAttributeData);
        }
    }
}
