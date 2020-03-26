package com.github.czyzby.lml.vis.parser.impl.tag;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.actor.ProgressBarLmlTag;
import com.github.czyzby.lml.parser.impl.tag.builder.FloatRangeLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisProgressBar;

/** Handles {@link VisProgressBar} actor. Expects that the text between its tags is a valid float - it will be set as
 * bar's value. Be careful though, as changing the value in such way might trigger registered change listeners. Mapped
 * to "progressBar", "visProgressBar".
 *
 * @author MJ */
public class VisProgressBarLmlTag extends ProgressBarLmlTag {
    public VisProgressBarLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected VisProgressBar getNewInstanceOfProgressBar(final FloatRangeLmlActorBuilder rangeBuilder) {
        return new VisProgressBar(rangeBuilder.getMin(), rangeBuilder.getMax(), rangeBuilder.getStepSize(),
                rangeBuilder.isVertical(), rangeBuilder.getStyleName());
    }
}
