package com.github.czyzby.lml.vis.parser.impl.tag.spinner;

import com.github.czyzby.kiwi.util.gdx.scene2d.Actors;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.FloatRangeLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import com.kotcrab.vis.ui.widget.spinner.SpinnerModel;

/** Constructs {@link Spinner} with {@link SimpleFloatSpinnerModel}. Mapped to "simpleFloatSpinner".
 *
 * @author MJ */
public class SimpleFloatSpinnerLmlTag extends AbstractSpinnerLmlTag {
    private SimpleFloatSpinnerModel model;

    public SimpleFloatSpinnerLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected LmlActorBuilder getNewInstanceOfBuilder() {
        final FloatRangeLmlActorBuilder rangeBuilder = new FloatRangeLmlActorBuilder();
        rangeBuilder.setStyleName(Actors.DEFAULT_STYLE);
        return rangeBuilder;
    }

    @Override
    protected SpinnerModel createModel(final LmlActorBuilder builder) {
        final FloatRangeLmlActorBuilder rangeBuilder = (FloatRangeLmlActorBuilder) builder;
        return model = new SimpleFloatSpinnerModel(rangeBuilder.getValue(), rangeBuilder.getMin(),
                rangeBuilder.getMax(), rangeBuilder.getStepSize(), 2);
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        model.setValue(getParser().parseFloat(plainTextLine, getActor()), false);
    }
}
