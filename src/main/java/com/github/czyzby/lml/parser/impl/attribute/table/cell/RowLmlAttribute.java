package com.github.czyzby.lml.parser.impl.attribute.table.cell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Calls {@link Cell#row()} (effectively adding a new row) if the passed value is a boolean true. Mapped to "row".
 *
 * @author MJ */
public class RowLmlAttribute extends AbstractCellLmlAttribute {
    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final Cell<?> cell,
            final String rawAttributeData) {
        if (parser.parseBoolean(rawAttributeData, actor)) {
            cell.row();
        }
    }
}
