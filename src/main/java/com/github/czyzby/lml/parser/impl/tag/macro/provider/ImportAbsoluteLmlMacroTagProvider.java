package com.github.czyzby.lml.parser.impl.tag.macro.provider;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.macro.ImportAbsoluteLmlMacroTag;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;

/** Provides absolute import macro tags.
 *
 * @author MJ */
public class ImportAbsoluteLmlMacroTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        return new ImportAbsoluteLmlMacroTag(parser, parentTag, rawTagData);
    }
}
