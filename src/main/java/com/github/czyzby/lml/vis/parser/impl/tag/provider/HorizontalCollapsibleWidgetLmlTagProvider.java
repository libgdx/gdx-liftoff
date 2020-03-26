package com.github.czyzby.lml.vis.parser.impl.tag.provider;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.HorizontalCollapsibleWidgetLmlTag;

/** Provides Vis horizontal collapsible widget tags.
 *
 * @author MJ */
public class HorizontalCollapsibleWidgetLmlTagProvider implements LmlTagProvider {
    @Override public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        return new HorizontalCollapsibleWidgetLmlTag(parser, parentTag, rawTagData);
    }
}
