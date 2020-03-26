package com.github.czyzby.lml.vis.parser.impl.tag.spinner;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.spinner.ArraySpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import com.kotcrab.vis.ui.widget.spinner.SpinnerModel;

/** Constructs {@link Spinner} with {@link ArraySpinnerModel}. Mapped to "arraySpinner".
 *
 * @author MJ */
public class ArraySpinnerLmlTag extends AbstractSpinnerLmlTag {
    private ArraySpinnerModel<Object> model;

    public ArraySpinnerLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected SpinnerModel createModel(final LmlActorBuilder builder) {
        return model = new ArraySpinnerModel<Object>();
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        model.getItems().add(getParser().parseString(plainTextLine, getActor()));
    }

    @Override
    protected void doOnTagClose() {
        model.invalidateDataSet();
    }
}
