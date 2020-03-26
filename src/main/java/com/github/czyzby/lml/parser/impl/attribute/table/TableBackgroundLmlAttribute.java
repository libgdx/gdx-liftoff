package com.github.czyzby.lml.parser.impl.attribute.table;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Table#setBackground(String)}. Expects a name of a Drawable in the skin used to construct the Table.
 * Mapped to "background", "bg.
 *
 * @author MJ */
public class TableBackgroundLmlAttribute implements LmlAttribute<Table> {
    @Override
    public Class<Table> getHandledType() {
        return Table.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Table actor, final String rawAttributeData) {
        actor.setBackground(parser.parseString(rawAttributeData, actor));
    }
}
