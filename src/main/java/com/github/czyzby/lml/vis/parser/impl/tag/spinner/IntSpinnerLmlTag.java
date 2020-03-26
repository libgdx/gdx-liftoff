package com.github.czyzby.lml.vis.parser.impl.tag.spinner;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.tag.builder.IntRangeLmlActorBuilder;
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import com.kotcrab.vis.ui.widget.spinner.SpinnerModel;

/** Constructs {@link Spinner} with {@link IntSpinnerModel}. Mapped to "intSpinner".
 *
 * @author MJ */
public class IntSpinnerLmlTag extends AbstractSpinnerLmlTag {
    private IntSpinnerModel model;

    public IntSpinnerLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected LmlActorBuilder getNewInstanceOfBuilder() {
        return new IntRangeLmlActorBuilder();
    }

    @Override
    protected SpinnerModel createModel(final LmlActorBuilder builder) {
        final IntRangeLmlActorBuilder rangeBuilder = (IntRangeLmlActorBuilder) builder;
        return new IntSpinnerModel(rangeBuilder.getValue(), rangeBuilder.getMin(), rangeBuilder.getMax(),
                rangeBuilder.getStep());
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        model.setValue(getParser().parseInt(plainTextLine, getActor()), false);
    }
}
