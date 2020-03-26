package com.github.czyzby.lml.parser.impl.attribute.table.cell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.github.czyzby.kiwi.util.common.Exceptions;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Cell#expand(boolean, boolean)}. Sets only Y value. Mapped to "expandY".
 *
 * @author MJ */
public class CellExpandYLmlAttribute extends AbstractCellLmlAttribute {
    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final Cell<?> cell,
            final String rawAttributeData) {
        cell.expand(determineExpandX(cell), parser.parseBoolean(rawAttributeData, actor));
    }

    protected boolean determineExpandX(final Cell<?> cell) {
        try {
            return cell.getExpandX() > 0;
        } catch (final Exception exception) {
            // LibGDX Scene2D method returns int, while the field is an Integer that might not have been initiated. This
            // causes a NPE - so when an exception is thrown, we assume that the expand was not set.
            Exceptions.ignore(exception);
            return false;
        }
    }
}
