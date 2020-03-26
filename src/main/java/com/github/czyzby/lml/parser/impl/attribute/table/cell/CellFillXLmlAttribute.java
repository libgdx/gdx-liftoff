package com.github.czyzby.lml.parser.impl.attribute.table.cell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.github.czyzby.kiwi.util.common.Exceptions;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Cell#fill(boolean, boolean)}. Sets only X value. Mapped to "fillX".
 *
 * @author MJ */
public class CellFillXLmlAttribute extends AbstractCellLmlAttribute {
    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final Cell<?> cell,
            final String rawAttributeData) {
        cell.fill(parser.parseBoolean(rawAttributeData, actor), determineFillY(cell));
    }

    protected boolean determineFillY(final Cell<?> cell) {
        try {
            return cell.getFillY() > 0f;
        } catch (final Exception exception) {
            // LibGDX Scene2D method returns float, while the field is a Float that might not have been initiated. This
            // causes a NPE - so when an exception is thrown, we assume that the fill was not set.
            Exceptions.ignore(exception);
            return false;
        }
    }
}
