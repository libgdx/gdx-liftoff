package com.github.czyzby.lml.parser.impl.attribute.table.tooltip;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.TooltipTable;

/** See {@link TooltipTable#setKeep(boolean)}. Expects a boolean. Allows to share the tooltip across multiple views.
 * Mapped to "keep".
 *
 * @author MJ */
public class KeepTooltipLmlAttribute implements LmlAttribute<TooltipTable> {
    @Override
    public Class<TooltipTable> getHandledType() {
        return TooltipTable.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final TooltipTable actor,
            final String rawAttributeData) {
        actor.setKeep(parser.parseBoolean(rawAttributeData, actor));
    }
}
