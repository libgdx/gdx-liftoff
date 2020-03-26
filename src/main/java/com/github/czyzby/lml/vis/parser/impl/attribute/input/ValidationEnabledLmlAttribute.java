package com.github.czyzby.lml.vis.parser.impl.attribute.input;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/** See {@link VisValidatableTextField#setValidationEnabled(boolean)}. Mapped to "enabled", "validate",
 * "validationEnabled".
 *
 * @author MJ */
public class ValidationEnabledLmlAttribute implements LmlAttribute<VisValidatableTextField> {
    @Override
    public Class<VisValidatableTextField> getHandledType() {
        return VisValidatableTextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisValidatableTextField actor,
            final String rawAttributeData) {
        actor.setValidationEnabled(parser.parseBoolean(rawAttributeData, actor));
    }
}
