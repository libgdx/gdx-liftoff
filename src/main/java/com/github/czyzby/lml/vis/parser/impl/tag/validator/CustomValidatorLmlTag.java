package com.github.czyzby.lml.vis.parser.impl.tag.validator;

import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.util.form.FormInputValidator;

/** Allows to create custom {@link InputValidator}s. Expects one attribute: name of a method that consumes a string and
 * returns a boolean. Mapped to "validator", "customValidator".
 *
 * @author MJ
 * @see com.github.czyzby.lml.vis.parser.impl.attribute.validator.CustomValidatorLmlAttribute */
public class CustomValidatorLmlTag extends AbstractValidatorLmlTag {
    private final CustomValidator validator = new CustomValidator();

    public CustomValidatorLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public InputValidator getInputValidator() {
        return validator;
    }

    @Override
    protected void doBeforeAttach(final InputValidator validator) {
        if (this.validator.validator == null) {
            getParser().throwError(
                    "Custom validator needs at least one attribute: ID of method that consumes a string and returns a boolean.");
        }
    }

    /** Wraps around an actor consumer, converting it into a {@link FormInputValidator};
     *
     * @author MJ */
    public static class CustomValidator extends FormInputValidator {
        private ActorConsumer<Boolean, String> validator;

        public CustomValidator() {
            super(Strings.EMPTY_STRING);
        }

        /** @param validator LML action used for validation. */
        public void setValidator(final ActorConsumer<Boolean, String> validator) {
            this.validator = validator;
        }

        @Override
        protected boolean validate(final String input) {
            return validator.consume(input);
        }
    }
}
