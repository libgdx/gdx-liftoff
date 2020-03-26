package com.github.czyzby.lml.parser.impl.tag.listener.provider;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.listener.ChangeListenerLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;

/** Provides change listener tags.
 *
 * @author MJ */
public class ChangeListenerLmlTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        return new ChangeListenerLmlTag(parser, parentTag, rawTagData);
    }
}
