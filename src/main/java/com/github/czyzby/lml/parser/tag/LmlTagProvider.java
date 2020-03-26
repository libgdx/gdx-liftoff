package com.github.czyzby.lml.parser.tag;

import com.github.czyzby.lml.parser.LmlParser;

/** Common interface for providers of tag wrappers.
 *
 * @author MJ */
public interface LmlTagProvider {
    /** @param parser requests creation of a tag.
     * @param parentTag direct parent of the tag. Might be null.
     * @param rawTagData unparsed LML data of the tag, containing its name, markers and attributes.
     * @return LML tag wrapper, allowing to process the tag. */
    LmlTag create(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData);
}
