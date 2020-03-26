package com.github.czyzby.lml.vis.parser.impl.attribute.grid;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.layout.GridGroup;

/** See {@link GridGroup#setItemSize(float)}. Mapped to "itemSize".
 *
 * @author MJ */
public class ItemSizeLmlAttribute implements LmlAttribute<GridGroup> {
    @Override
    public Class<GridGroup> getHandledType() {
        return GridGroup.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final GridGroup actor,
            final String rawAttributeData) {
        actor.setItemSize(parser.parseFloat(rawAttributeData, actor));
    }
}
