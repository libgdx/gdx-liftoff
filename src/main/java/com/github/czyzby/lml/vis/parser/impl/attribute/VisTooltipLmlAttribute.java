package com.github.czyzby.lml.vis.parser.impl.attribute;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.Tooltip;

/** Attaches a {@link Tooltip} with the default style to the chosen actor. Attribute expects a string value that will be
 * added as a label with default style to the tooltip. Can be attached to any widget. VisUI-based equivalent of
 * {@link com.github.czyzby.lml.parser.impl.attribute.TooltipLmlAttribute}. Mapped to "visTooltip".
 *
 * @author MJ */
public class VisTooltipLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        final Tooltip tooltip = new Tooltip();
        tooltip.clearChildren(); // Removing empty cell with predefined paddings.
        tooltip.add(parser.parseString(rawAttributeData, actor));
        tooltip.pack();
        tooltip.setTarget(actor);
    }
}
