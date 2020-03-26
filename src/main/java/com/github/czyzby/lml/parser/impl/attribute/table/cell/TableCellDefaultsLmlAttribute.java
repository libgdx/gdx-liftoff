package com.github.czyzby.lml.parser.impl.attribute.table.cell;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Allows to turn any cell attribute into an attribute applied to {@link Table#defaults()} cell.
 *
 * @author MJ */
public class TableCellDefaultsLmlAttribute implements LmlAttribute<Table> {
    private final AbstractCellLmlAttribute baseAttribute;

    /** @param baseAttribute used to parse the attribute. */
    public TableCellDefaultsLmlAttribute(final AbstractCellLmlAttribute baseAttribute) {
        this.baseAttribute = baseAttribute;
    }

    @Override
    public Class<Table> getHandledType() {
        return Table.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Table actor, final String rawAttributeData) {
        baseAttribute.process(parser, tag, actor, actor.defaults(), rawAttributeData);
    }
}
