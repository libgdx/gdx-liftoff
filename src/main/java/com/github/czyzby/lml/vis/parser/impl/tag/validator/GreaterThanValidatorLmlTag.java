package com.github.czyzby.lml.vis.parser.impl.tag.validator;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.util.Validators;

/** Provides {@link Validators.GreaterThanValidator}s. Unless "value" attribute is provided, will validate if number is
 * greater than 0. Mapped to "greaterThan", "greaterThanValidator".
 *
 * @author MJ */
public class GreaterThanValidatorLmlTag extends AbstractValidatorLmlTag {
    public GreaterThanValidatorLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public InputValidator getInputValidator() {
        return new Validators.GreaterThanValidator(0f);
    }
}
