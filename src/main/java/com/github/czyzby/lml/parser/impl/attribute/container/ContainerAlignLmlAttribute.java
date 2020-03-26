package com.github.czyzby.lml.parser.impl.attribute.container;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** See {@link Container#align(int)}. Expends string matching name of
 * {@link com.github.czyzby.kiwi.util.gdx.scene2d.Alignment} enum constant. If container is in a table cell, also calls
 * {@link Cell#align(int)}. Mapped to "align".
 *
 * @author MJ */
public class ContainerAlignLmlAttribute extends AbstractSharedContainerAndCellLmlAttribute {
    @Override
    protected void applyToContainer(final LmlParser parser, final LmlTag tag, final Container<?> actor,
            final String rawAttributeData) {
        actor.align(LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
    }

    @Override
    protected void applyToCell(final Container<?> actor, final Cell<?> cell) {
        cell.align(actor.getAlign());
    }
}
