package com.github.czyzby.lml.parser.impl.attribute.table.tooltip;

import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.TooltipTable;

/** See {@link Tooltip#setInstant(boolean)}. Mapped to "instant".
 *
 * @author MJ */
public class InstantLmlAttribute implements LmlAttribute<TooltipTable> {
    @Override
    public Class<TooltipTable> getHandledType() {
        return TooltipTable.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final TooltipTable actor,
            final String rawAttributeData) {
        final Tooltip<TooltipTable> tooltip = actor.getTooltip();
        tooltip.setInstant(parser.parseBoolean(rawAttributeData, tooltip));
    }
}
