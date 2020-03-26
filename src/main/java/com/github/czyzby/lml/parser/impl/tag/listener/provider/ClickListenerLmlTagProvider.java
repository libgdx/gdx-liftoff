package com.github.czyzby.lml.parser.impl.tag.listener.provider;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.listener.ClickListenerLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;

/** Provides click listener tags.
 *
 * @author MJ */
public class ClickListenerLmlTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        return new ClickListenerLmlTag(parser, parentTag, rawTagData);
    }
}
