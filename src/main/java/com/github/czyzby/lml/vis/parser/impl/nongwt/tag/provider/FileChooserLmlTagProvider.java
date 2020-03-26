package com.github.czyzby.lml.vis.parser.impl.nongwt.tag.provider;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.nongwt.tag.FileChooserLmlTag;

/** Provides Vis file chooser tags.
 *
 * @author MJ */
public class FileChooserLmlTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        return new FileChooserLmlTag(parser, parentTag, rawTagData);
    }
}
