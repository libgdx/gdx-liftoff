package com.github.czyzby.lml.parser;

/** Contains markers and special characters of LML style sheet files.
 *
 * @author MJ */
public interface LssSyntax {
    /** @return if tag name starts with this character, instead of appending styles to its sheet, its styles will be
     *         copied and applied to other tags. Defaults to '.'. */
    char getInheritanceMarker();

    /** @return character that starts block containing style definitions. Defaults to '{'. */
    char getBlockOpening();

    /** @return character that ends block containing style definitions. Defaults to '}'. */
    char getBlockClosing();

    /** @return character separating attribute name from its default value. Defaults to ':'. */
    char getSeparator();

    /** @return character ending attribute definition, typically at the end of the line. Defaults to ';'. */
    char getLineEnd();

    /** @return character used to separate multiple tag name definitions before style block. Defaults to ','. */
    char getTagSeparator();

    /** @return character that opens and closes comments. Defaults to '/'.
     * @see #getSecondaryCommentMarker() */
    char getCommentMarker();

    /** @return character that has to appear after or before comment marker to properly open or close the comments.
     *         Defaults to '*'.
     * @see #getCommentMarker() */
    char getSecondaryCommentMarker();
}
