package com.github.czyzby.lml.vis.parser.impl.attribute.input;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter;

/** See {@link VisTextField#setTextFieldFilter(TextFieldFilter)}. Expects a boolean. If attribute's value is true,
 * {@link TextFieldFilter.DigitsOnlyFilter} will be set as text field's character filter. Mapped to "digitsOnly",
 * "numeric".
 *
 * @author MJ */
public class DigitsOnlyLmlAttribute implements LmlAttribute<VisTextField> {
    @Override
    public Class<VisTextField> getHandledType() {
        return VisTextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisTextField actor,
            final String rawAttributeData) {
        if (parser.parseBoolean(rawAttributeData, actor)) {
            actor.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        }
    }
}
