package com.github.czyzby.lml.parser.impl.attribute.table.cell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** See {@link Cell#height(Value)}. See {@link LmlUtilities#parseHorizontalValue(LmlParser, LmlTag, Actor, String)} and
 * {@link LmlUtilities#parseVerticalValue(LmlParser, LmlTag, Actor, String)} for more info on value parsing. Mapped to
 * "height".
 *
 * @author MJ */
public class CellHeightLmlAttribute extends AbstractCellLmlAttribute {
    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final Cell<?> cell,
            final String rawAttributeData) {
        final Value verticalValue = LmlUtilities.parseVerticalValue(parser, tag.getParent(), actor, rawAttributeData);
        cell.height(verticalValue);
    }

    @Override
    protected void processForActor(final LmlParser parser, final LmlTag tag, final Actor actor,
            final String rawAttributeData) {
        actor.setHeight(parser.parseFloat(rawAttributeData, actor));
    }
}
