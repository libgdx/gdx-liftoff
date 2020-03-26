package com.github.czyzby.lml.vis.parser.impl.attribute.grid.fixed;

import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.ui.FixedSizeGridGroup;

/** See {@link FixedSizeGridGroup#setBlockedIndex(int)}. Expects an array of ints. Mapped to "blockIndexes".
 *
 * @author MJ */
public class BlockIndexesLmlAttribute implements LmlAttribute<FixedSizeGridGroup> {
    @Override
    public Class<FixedSizeGridGroup> getHandledType() {
        return FixedSizeGridGroup.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final FixedSizeGridGroup actor,
            final String rawAttributeData) {
        final String[] array = parser.parseArray(rawAttributeData, actor);
        for (final String element : array) {
            if (Strings.isNotBlank(element)) {
                actor.setBlockedIndex(Integer.parseInt(element));
            }
        }
    }
}
