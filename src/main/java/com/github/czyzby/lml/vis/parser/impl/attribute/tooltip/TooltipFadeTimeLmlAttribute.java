package com.github.czyzby.lml.vis.parser.impl.attribute.tooltip;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.Tooltip;

/** See {@link Tooltip#setFadeTime(float)}. Mapped to "fadeTime", "fadingTime".
 *
 * @author MJ */
public class TooltipFadeTimeLmlAttribute implements LmlAttribute<Tooltip> {
    @Override
    public Class<Tooltip> getHandledType() {
        return Tooltip.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Tooltip actor, final String rawAttributeData) {
        actor.setFadeTime(parser.parseFloat(rawAttributeData, actor));
    }
}
