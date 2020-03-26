package com.github.czyzby.lml.vis.parser.impl.tag.provider;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.ScrollableTextAreaLmlTag;

/**
 * Provides scrollable text area widget tags.
 * @author MJ
 */
public class ScrollableTextAreaLmlTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        return new ScrollableTextAreaLmlTag(parser, parentTag, rawTagData);
    }
}
