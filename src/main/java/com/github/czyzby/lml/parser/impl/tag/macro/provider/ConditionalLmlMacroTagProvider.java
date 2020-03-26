package com.github.czyzby.lml.parser.impl.tag.macro.provider;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.macro.ConditionalLmlMacroTag;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;

/** Provides conditional macro tags.
 *
 * @author MJ */
public class ConditionalLmlMacroTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        return new ConditionalLmlMacroTag(parser, parentTag, rawTagData);
    }
}
