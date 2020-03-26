package com.github.czyzby.lml.parser.impl.tag;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Utility abstract class for widgets that simply cannot have children and should never have child tags or plain text
 * between tags. Throws errors if parser is strict.
 *
 * @author MJ */
public abstract class AbstractNonParentalActorLmlTag extends AbstractActorLmlTag {
    public AbstractNonParentalActorLmlTag(final LmlParser parser, final LmlTag parentTag,
            final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        getParser().throwErrorIfStrict(
                getTagName() + " tag cannot handle children and should not be parental. Received child tag: "
                        + childTag.getTagName() + " with actor: " + childTag.getActor());
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        getParser().throwErrorIfStrict(getTagName()
                + " tag cannot handle children and should not be parental. Received plain text data between tags that cannot be parsed: "
                + plainTextLine);
    }
}
