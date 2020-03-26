package com.github.czyzby.lml.vis.parser.impl.attribute.grid.fixed;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.ui.FixedSizeGridGroup;

/** See {@link FixedSizeGridGroup#setItemsAmount(int)}. Mapped to "itemsAmount".
 *
 * @author MJ */
public class ItemsAmountLmlAttribute implements LmlAttribute<FixedSizeGridGroup> {
    @Override
    public Class<FixedSizeGridGroup> getHandledType() {
        return FixedSizeGridGroup.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final FixedSizeGridGroup actor,
            final String rawAttributeData) {
        actor.setItemsAmount(parser.parseInt(rawAttributeData, actor));
    }
}
