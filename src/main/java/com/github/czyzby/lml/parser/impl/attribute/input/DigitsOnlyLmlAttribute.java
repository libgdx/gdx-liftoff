package com.github.czyzby.lml.parser.impl.attribute.input;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link TextField#setTextFieldFilter(TextFieldFilter)}. Expects a boolean. If attribute's value is true,
 * {@link TextFieldFilter.DigitsOnlyFilter} will be set as text field's character filter. Mapped to "digitsOnly",
 * "numeric".
 *
 * @author MJ */
public class DigitsOnlyLmlAttribute implements LmlAttribute<TextField> {
    @Override
    public Class<TextField> getHandledType() {
        return TextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final TextField actor,
            final String rawAttributeData) {
        if (parser.parseBoolean(rawAttributeData, actor)) {
            actor.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        }
    }
}
