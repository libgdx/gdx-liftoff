package com.github.czyzby.lml.vis.parser.impl.attribute.tooltip;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.Tooltip;

/** See {@link Tooltip#setMouseMoveFadeOut(boolean)}. Mapped to "mouseMoveFadeOut".
 *
 * @author MJ */
public class MouseMoveFadeOutLmlAttribute implements LmlAttribute<Tooltip> {
    @Override
    public Class<Tooltip> getHandledType() {
        return Tooltip.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Tooltip actor, final String rawAttributeData) {
        actor.setMouseMoveFadeOut(parser.parseBoolean(rawAttributeData, actor));
    }
}
