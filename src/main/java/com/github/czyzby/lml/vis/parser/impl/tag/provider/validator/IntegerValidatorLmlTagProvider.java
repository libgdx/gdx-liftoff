package com.github.czyzby.lml.vis.parser.impl.tag.provider.validator;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.validator.IntegerValidatorLmlTag;

/** Provides integer validator tags.
 *
 * @author MJ */
public class IntegerValidatorLmlTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        return new IntegerValidatorLmlTag(parser, parentTag, rawTagData);
    }
}
