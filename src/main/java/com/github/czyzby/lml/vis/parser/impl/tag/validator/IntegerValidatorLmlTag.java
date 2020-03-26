package com.github.czyzby.lml.vis.parser.impl.tag.validator;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.util.Validators;

/** Provides {@link Validators.IntegerValidator}s Cannot append children. Mapped to "integerValidator", "intValidator"
 * ", "isInt", "isInteger".
 *
 * @author MJ */
public class IntegerValidatorLmlTag extends AbstractValidatorLmlTag {
    public IntegerValidatorLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public InputValidator getInputValidator() {
        return Validators.INTEGERS;
    }
}