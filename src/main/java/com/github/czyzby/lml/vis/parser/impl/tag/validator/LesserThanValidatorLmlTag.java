package com.github.czyzby.lml.vis.parser.impl.tag.validator;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.util.Validators;

/** Provides {@link Validators.LesserThanValidator}s. Unless "value" attribute is provided, will validate if number is
 * lower than 0. Mapped to "lesserThan", "lesserThanValidator".
 *
 * @author MJ */
public class LesserThanValidatorLmlTag extends AbstractValidatorLmlTag {
    public LesserThanValidatorLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public InputValidator getInputValidator() {
        return new Validators.LesserThanValidator(0f);
    }
}
