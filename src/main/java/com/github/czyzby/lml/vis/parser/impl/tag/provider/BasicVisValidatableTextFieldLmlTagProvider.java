package com.github.czyzby.lml.vis.parser.impl.tag.provider;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.BasicVisValidatableTextFieldLmlTag;
import com.github.czyzby.lml.vis.parser.impl.tag.VisValidatableTextFieldLmlTag;

/** Provides Vis basic validatable text field tags.
 *
 * @author MJ */
public class BasicVisValidatableTextFieldLmlTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        return new BasicVisValidatableTextFieldLmlTag(parser, parentTag, rawTagData);
    }
}
