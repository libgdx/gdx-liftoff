package com.github.czyzby.lml.parser.impl.attribute.table.cell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.github.czyzby.kiwi.util.common.Exceptions;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Cell#uniform(boolean, boolean)}. Sets only Y value. Mapped to "uniformY".
 *
 * @author MJ */
public class CellUniformYLmlAttribute extends AbstractCellLmlAttribute {
    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final Cell<?> cell,
            final String rawAttributeData) {
        cell.uniform(determineUniformX(cell), parser.parseBoolean(rawAttributeData, actor));
    }

    protected boolean determineUniformX(final Cell<?> cell) {
        try {
            return cell.getUniformX();
        } catch (final Exception exception) {
            // libGDX Scene2D method returns boolean, while the field is a Boolean that might not have been initiated.
            // This causes a NPE - so when an exception is thrown, we assume that the uniform was not set.
            Exceptions.ignore(exception);
            return false;
        }
    }
}
