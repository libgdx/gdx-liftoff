package com.github.czyzby.lml.vis.parser.impl.attribute.spinner;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.spinner.FloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import com.kotcrab.vis.ui.widget.spinner.SpinnerModel;

/** See {@link FloatSpinnerModel#setScale(int)} and {@link SimpleFloatSpinnerModel#setPrecision(int)}. Mapped to
 * "scale", "precision".
 *
 * @author MJ */
public class SpinnerPrecisionLmlAttribute implements LmlAttribute<Spinner> {
    @Override
    public Class<Spinner> getHandledType() {
        return Spinner.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Spinner actor, final String rawAttributeData) {
        final SpinnerModel model = actor.getModel();
        final int precision = parser.parseInt(rawAttributeData, actor);
        if (model instanceof FloatSpinnerModel) {
            ((FloatSpinnerModel) model).setScale(precision);
        } else if (model instanceof SimpleFloatSpinnerModel) {
            ((SimpleFloatSpinnerModel) model).setPrecision(precision);
        } else {
            parser.throwErrorIfStrict(
                    "Precision (scale) attribute can be used only for spinners with models storing decimal values. Found attribute in tag: "
                            + tag.getTagName());
        }
    }
}
