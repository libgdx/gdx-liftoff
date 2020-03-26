package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractNonParentalActorLmlTag;
import com.github.czyzby.lml.parser.impl.tag.builder.FloatRangeLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlParsingException;

/** Handles {@link ProgressBar} actor. Expects that the text between its tags is a valid float - it will be set as bar's
 * value. Be careful though, as changing the value in such way might trigger registered change listeners. Mapped to
 * "progressBar".
 *
 * @author MJ */
public class ProgressBarLmlTag extends AbstractNonParentalActorLmlTag {
    public ProgressBarLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected FloatRangeLmlActorBuilder getNewInstanceOfBuilder() {
        return new FloatRangeLmlActorBuilder();
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        try {
            final float value = getParser().parseFloat(plainTextLine);
            getProgessBar().setValue(value);
        } catch (final LmlParsingException exception) {
            // Expected if input is not a float.
            if (getParser().isStrict()) {
                throw exception;
            }
        }
    }

    /** @return actor instance, casted for convenience. */
    protected ProgressBar getProgessBar() {
        return (ProgressBar) getActor();
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        final FloatRangeLmlActorBuilder rangeBuilder = (FloatRangeLmlActorBuilder) builder;
        rangeBuilder.validateRange(getParser());
        final ProgressBar actor = getNewInstanceOfProgressBar(rangeBuilder);
        actor.setValue(rangeBuilder.getValue());
        return actor;
    }

    /** @param rangeBuilder contains data necessary to construct a float-range-based widget.
     * @return a new instance of progress bar. */
    protected ProgressBar getNewInstanceOfProgressBar(final FloatRangeLmlActorBuilder rangeBuilder) {
        return new ProgressBar(rangeBuilder.getMin(), rangeBuilder.getMax(), rangeBuilder.getStepSize(),
                rangeBuilder.isVertical(), getSkin(rangeBuilder), rangeBuilder.getStyleName());
    }
}
