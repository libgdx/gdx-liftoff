package com.github.czyzby.lml.parser.impl.attribute.table.tooltip;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.TooltipTable;

/** See {@link TooltipTable#setIds(String[])}. Expects an array of actor IDs that the tooltip should be attached to.
 * Mapped to "ids".
 *
 * @author MJ */
public class TooltipIdsLmlAttribute implements LmlAttribute<TooltipTable> {
    @Override
    public Class<TooltipTable> getHandledType() {
        return TooltipTable.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final TooltipTable actor,
            final String rawAttributeData) {
        actor.setIds(parser.parseArray(rawAttributeData, actor));
    }
}
