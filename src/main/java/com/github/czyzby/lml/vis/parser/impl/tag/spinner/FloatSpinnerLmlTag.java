package com.github.czyzby.lml.vis.parser.impl.tag.spinner;

import java.math.BigDecimal;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.tag.builder.StringRangeLmlActorBuilder;
import com.kotcrab.vis.ui.widget.spinner.FloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import com.kotcrab.vis.ui.widget.spinner.SpinnerModel;

/** Constructs {@link Spinner} with {@link FloatSpinnerModel}. Data is stored in a {@link BigDecimal} internally. Mapped
 * to "floatSpinner", "spinner".
 *
 * @author MJ */
public class FloatSpinnerLmlTag extends AbstractSpinnerLmlTag {
    private FloatSpinnerModel model;

    public FloatSpinnerLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected LmlActorBuilder getNewInstanceOfBuilder() {
        return new StringRangeLmlActorBuilder();
    }

    @Override
    protected SpinnerModel createModel(final LmlActorBuilder builder) {
        final StringRangeLmlActorBuilder rangeBuilder = (StringRangeLmlActorBuilder) builder;
        return new FloatSpinnerModel(rangeBuilder.getValue(), rangeBuilder.getMin(), rangeBuilder.getMax(),
                rangeBuilder.getStep(), 2);
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        try {
            model.setValue(new BigDecimal(getParser().parseString(plainTextLine, getActor())), false);
        } catch (final NumberFormatException exception) {
            getParser().throwErrorIfStrict("Invalid spinner data, big decimal expected: " + plainTextLine, exception);
        }
    }
}
