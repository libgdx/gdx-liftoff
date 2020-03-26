package com.github.czyzby.lml.parser.impl.tag.listener.provider;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.listener.InputListenerLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;

/** Provides input listener tags.
 *
 * @author MJ */
public class InputListenerLmlTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        return new InputListenerLmlTag(parser, parentTag, rawTagData);
    }
}
