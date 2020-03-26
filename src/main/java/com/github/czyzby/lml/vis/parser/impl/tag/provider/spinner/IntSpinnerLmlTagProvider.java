package com.github.czyzby.lml.vis.parser.impl.tag.provider.spinner;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.spinner.IntSpinnerLmlTag;

/** Provides spinner tags with int range models.
 *
 * @author MJ */
public class IntSpinnerLmlTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        return new IntSpinnerLmlTag(parser, parentTag, rawTagData);
    }
}