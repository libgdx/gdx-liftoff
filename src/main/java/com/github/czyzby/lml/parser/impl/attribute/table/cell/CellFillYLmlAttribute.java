package com.github.czyzby.lml.parser.impl.attribute.table.cell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.github.czyzby.kiwi.util.common.Exceptions;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Cell#fill(boolean, boolean)}. Sets only Y value. Mapped to "fillY".
 *
 * @author MJ */
public class CellFillYLmlAttribute extends AbstractCellLmlAttribute {
    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final Cell<?> cell,
            final String rawAttributeData) {
        cell.fill(determineFillX(cell), parser.parseBoolean(rawAttributeData, actor));
    }

    protected boolean determineFillX(final Cell<?> cell) {
        try {
            return cell.getFillX() > 0f;
        } catch (final Exception exception) {
            // libGDX Scene2D method returns float, while the field is a Float that might not have been initiated. This
            // causes a NPE - so when an exception is thrown, we assume that the fill was not set.
            Exceptions.ignore(exception);
            return false;
        }
    }
}
