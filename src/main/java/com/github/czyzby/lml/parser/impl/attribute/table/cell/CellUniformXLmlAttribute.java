package com.github.czyzby.lml.parser.impl.attribute.table.cell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.github.czyzby.kiwi.util.common.Exceptions;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Cell#uniform(boolean, boolean)}. Sets only X value. Mapped to "uniformX".
 *
 * @author MJ */
public class CellUniformXLmlAttribute extends AbstractCellLmlAttribute {
    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final Cell<?> cell,
            final String rawAttributeData) {
        cell.uniform(parser.parseBoolean(rawAttributeData, actor), determineUniformY(cell));
    }

    protected boolean determineUniformY(final Cell<?> cell) {
        try {
            return cell.getUniformY();
        } catch (final Exception exception) {
            // LibGDX Scene2D method returns boolean, while the field is a Boolean that might not have been initiated.
            // This causes a NPE - so when an exception is thrown, we assume that the uniform was not set.
            Exceptions.ignore(exception);
            return false;
        }
    }
}
