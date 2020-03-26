package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.FloatRangeLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Handles {@link Slider} actor. Expects that the text between its tags is a valid float - it will be set as bar's
 * value. Be careful though, as changing the value in such way might trigger registered change listeners. Mapped to
 * "slider".
 *
 * @author MJ */
public class SliderLmlTag extends ProgressBarLmlTag {
    public SliderLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected ProgressBar getNewInstanceOfProgressBar(final FloatRangeLmlActorBuilder rangeBuilder) {
        return new Slider(rangeBuilder.getMin(), rangeBuilder.getMax(), rangeBuilder.getStepSize(),
                rangeBuilder.isVertical(), getSkin(rangeBuilder), rangeBuilder.getStyleName());
    }
}
