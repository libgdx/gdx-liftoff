package com.github.czyzby.lml.vis.parser.impl.nongwt.tag.provider.validator;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.nongwt.tag.validator.FileValidatorLmlTag;

/** Provides Vis file validator tags.
 *
 * @author MJ */
public class FileValidatorLmlTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        return new FileValidatorLmlTag(parser, parentTag, rawTagData);
    }
}
