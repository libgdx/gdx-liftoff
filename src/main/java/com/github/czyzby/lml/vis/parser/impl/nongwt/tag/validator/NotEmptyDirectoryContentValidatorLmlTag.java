package com.github.czyzby.lml.vis.parser.impl.nongwt.tag.validator;

import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.tag.validator.AbstractValidatorLmlTag;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.util.form.FormValidator;

/** Attaches {@link FormValidator.DirectoryContentValidator}s. Checks if the passed text is a non-empty directory.
 * Mapped to "notEmptyDirectoryValidator", "isDirectoryNotEmpty".
 *
 * @author MJ */
public class NotEmptyDirectoryContentValidatorLmlTag extends AbstractValidatorLmlTag {
    public NotEmptyDirectoryContentValidatorLmlTag(final LmlParser parser, final LmlTag parentTag,
            final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public InputValidator getInputValidator() {
        return new FormValidator.DirectoryContentValidator(Strings.EMPTY_STRING, false);
    }
}
