package com.github.czyzby.lml.vis.parser.impl.tag.provider;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.CollapsibleWidgetLmlTag;

/** Provides Vis collapsible widget tags.
 *
 * @author MJ */
public class CollapsibleWidgetLmlTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        return new CollapsibleWidgetLmlTag(parser, parentTag, rawTagData);
    }
}
