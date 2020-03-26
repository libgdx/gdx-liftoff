package com.github.czyzby.lml.vis.parser.impl.attribute.validator;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.util.form.FormInputValidator;

/** See {@link FormInputValidator#setErrorMsg(String)}. Can be used for all validators: if a standard validator is used
 * in a form, it will be wrapped with {@link FormInputValidator} and properly parse this attribute. Mapped to "error",
 * "errorMsg", "errorMessage", "formError".
 *
 * @author MJ */
public class ErrorMessageLmlAttribute implements LmlAttribute<FormInputValidator> {
    @Override
    public Class<FormInputValidator> getHandledType() {
        return FormInputValidator.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final FormInputValidator actor,
            final String rawAttributeData) {
        actor.setErrorMsg(parser.parseString(rawAttributeData, tag.getParent().getActor()));
    }
}
