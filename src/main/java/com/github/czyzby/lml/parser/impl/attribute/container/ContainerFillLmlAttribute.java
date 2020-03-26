package com.github.czyzby.lml.parser.impl.attribute.container;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Container#fill(boolean)}. If container is in a table cell, also calls {@link Cell#fill(boolean)}. Mapped
 * to "fill".
 *
 * @author MJ */
public class ContainerFillLmlAttribute extends AbstractSharedContainerAndCellLmlAttribute {
    @Override
    protected void applyToContainer(final LmlParser parser, final LmlTag tag, final Container<?> actor,
            final String rawAttributeData) {
        actor.fill(parser.parseBoolean(rawAttributeData, actor));
    }

    @Override
    protected void applyToCell(final Container<?> actor, final Cell<?> cell) {
        cell.fill(actor.getFillX(), actor.getFillY());
    }
}
