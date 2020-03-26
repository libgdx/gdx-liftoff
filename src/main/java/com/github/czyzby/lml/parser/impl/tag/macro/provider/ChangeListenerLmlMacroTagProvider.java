package com.github.czyzby.lml.parser.impl.tag.macro.provider;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.macro.ChangeListenerLmlMacroTag;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;

/** Provides change listener macro tags.
 *
 * @author MJ */
public class ChangeListenerLmlMacroTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        return new ChangeListenerLmlMacroTag(parser, parentTag, rawTagData);
    }
}
