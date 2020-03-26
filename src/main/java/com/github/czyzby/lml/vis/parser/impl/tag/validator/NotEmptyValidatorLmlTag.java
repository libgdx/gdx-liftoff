package com.github.czyzby.lml.vis.parser.impl.tag.validator;

import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator.EmptyInputValidator;

/** Provider {@link EmptyInputValidator}s. Returns false if input is empty. Mapped to "notEmpty", "notEmptyValidator",
 * "isNotEmpty".
 *
 * @author MJ */
public class NotEmptyValidatorLmlTag extends AbstractValidatorLmlTag {
    public NotEmptyValidatorLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public InputValidator getInputValidator() {
        return new EmptyInputValidator(Strings.EMPTY_STRING);
    }
}
