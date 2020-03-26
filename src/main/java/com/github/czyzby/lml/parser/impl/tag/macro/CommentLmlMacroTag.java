package com.github.czyzby.lml.parser.impl.tag.macro;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Utility macro that ignores its content completely. Can be used as an alternative way to add comments - especially
 * since it supports nested comments out of the box.
 *
 * @author MJ */
public class CommentLmlMacroTag extends AbstractMacroLmlTag {
    public CommentLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawMacroContent) {
        // Macro content is ignored, effectively turning data between its tags into a comment.
    }
}
