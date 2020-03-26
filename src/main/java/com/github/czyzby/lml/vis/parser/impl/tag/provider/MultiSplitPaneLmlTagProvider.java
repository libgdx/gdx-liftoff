package com.github.czyzby.lml.vis.parser.impl.tag.provider;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.MultiSplitPaneLmlTag;

/**
 * Provides multi split pane widget tags.
 * @author MJ
 */
public class MultiSplitPaneLmlTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        return new MultiSplitPaneLmlTag(parser, parentTag, rawTagData);
    }
}
