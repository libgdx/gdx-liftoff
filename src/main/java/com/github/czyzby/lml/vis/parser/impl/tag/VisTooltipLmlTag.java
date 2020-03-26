package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.Tooltip;

/** Handles attachable {@link Tooltip} widgets. Can be a child of any widget. Mapped to "visTooltip".
 *
 * @author MJ */
public class VisTooltipLmlTag extends VisTableLmlTag {
    public VisTooltipLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        final Tooltip tooltip = new Tooltip(builder.getStyleName());
        tooltip.clearChildren(); // One empty cell is added, removing it.
        return tooltip;
    }

    @Override
    public boolean isAttachable() {
        return true;
    }

    @Override
    public void attachTo(final LmlTag tag) {
        final Tooltip tooltip = (Tooltip) getActor();
        tooltip.pack();
        tooltip.setTarget(tag.getActor());
    }
}
