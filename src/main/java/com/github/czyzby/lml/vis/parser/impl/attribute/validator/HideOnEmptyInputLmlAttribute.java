package com.github.czyzby.lml.vis.parser.impl.attribute.validator;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.util.form.FormInputValidator;

/** See {@link FormInputValidator#setHideErrorOnEmptyInput(boolean)}. Mapped to "hideOnEmpty", "hideErrorOnEmpty".
 *
 * @author MJ */
public class HideOnEmptyInputLmlAttribute implements LmlAttribute<FormInputValidator> {
    @Override
    public Class<FormInputValidator> getHandledType() {
        return FormInputValidator.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final FormInputValidator actor,
            final String rawAttributeData) {
        actor.setHideErrorOnEmptyInput(parser.parseBoolean(rawAttributeData, tag.getParent().getActor()));
    }
}
