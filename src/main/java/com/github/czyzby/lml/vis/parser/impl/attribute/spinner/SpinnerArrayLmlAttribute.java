package com.github.czyzby.lml.vis.parser.impl.attribute.spinner;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.spinner.ArraySpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

/** See {@link ArraySpinnerModel#getItems()}. Expects a LML array of values that will be added to the model's array.
 * Mapped to "values".
 *
 * @author MJ */
public class SpinnerArrayLmlAttribute implements LmlAttribute<Spinner> {
    @Override
    public Class<Spinner> getHandledType() {
        return Spinner.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process(final LmlParser parser, final LmlTag tag, final Spinner actor, final String rawAttributeData) {
        if (!(actor.getModel() instanceof ArraySpinnerModel<?>)) {
            parser.throwErrorIfStrict(
                    "Spinner array attribute is available only for spinners with ArraySpinnerModel. Found array attribute on tag: "
                            + tag.getTagName());
            return;
        }
        final ArraySpinnerModel<Object> model = (ArraySpinnerModel<Object>) actor.getModel();
        model.getItems().addAll((Object[]) parser.fullyParseArray(rawAttributeData, actor));
    }
}
