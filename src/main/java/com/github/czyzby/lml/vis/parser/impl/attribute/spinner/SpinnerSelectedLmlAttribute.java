package com.github.czyzby.lml.vis.parser.impl.attribute.spinner;

import java.math.BigDecimal;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.widget.spinner.ArraySpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.FloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import com.kotcrab.vis.ui.widget.spinner.SpinnerModel;

/** In case of numeric spinners, this attribute allows to set the initial value. In case of array spinners, this marks
 * the index of the value that should be selected first. In case of numeric spinners, "value" attribute is preferred, as
 * it will use model's constructor: use this attribute only if you need to invoke a method that consumes spinner
 * instance. Mapped to "selected".
 *
 * @author MJ */
public class SpinnerSelectedLmlAttribute implements LmlAttribute<Spinner> {
    @Override
    public Class<Spinner> getHandledType() {
        return Spinner.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Spinner actor, final String rawAttributeData) {
        LmlUtilities.getLmlUserObject(actor).addOnCloseAction(new ActorConsumer<Void, Object>() {
            @Override
            public Void consume(final Object spinner) {
                final SpinnerModel model = actor.getModel();
                if (model instanceof IntSpinnerModel) {
                    ((IntSpinnerModel) model).setValue(parser.parseInt(rawAttributeData, actor), false);
                } else if (model instanceof FloatSpinnerModel) {
                    try {
                        ((FloatSpinnerModel) model)
                                .setValue(new BigDecimal(parser.parseString(rawAttributeData, actor)), false);
                    } catch (final NumberFormatException exception) {
                        parser.throwErrorIfStrict("Invalid numeric value: " + rawAttributeData, exception);
                    }
                } else if (model instanceof SimpleFloatSpinnerModel) {
                    ((SimpleFloatSpinnerModel) model).setValue(parser.parseFloat(rawAttributeData, actor), false);
                } else if (model instanceof ArraySpinnerModel<?>) {
                    ((ArraySpinnerModel<?>) model).setCurrent(parser.parseInt(rawAttributeData, actor), false);
                } else {
                    parser.throwErrorIfStrict("Unknown model: " + model + ". Unable to select value.");
                }
                actor.notifyValueChanged(false);
                return null;
            }
        });
    }
}
