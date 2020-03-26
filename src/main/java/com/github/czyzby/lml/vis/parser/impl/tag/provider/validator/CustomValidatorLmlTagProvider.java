package com.github.czyzby.lml.vis.parser.impl.tag.provider.validator;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.validator.CustomValidatorLmlTag;

/** Provides custom validator tags.
 *
 * @author MJ */
public class CustomValidatorLmlTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        return new CustomValidatorLmlTag(parser, parentTag, rawTagData);
    }
}
