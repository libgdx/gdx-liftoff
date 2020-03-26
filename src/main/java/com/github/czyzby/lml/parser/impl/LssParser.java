package com.github.czyzby.lml.parser.impl;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.LmlStyleSheet;
import com.github.czyzby.lml.parser.LmlTemplateReader;
import com.github.czyzby.lml.parser.LssSyntax;

/** Allows to process LML style sheet code (with CSS-like syntax), extracting default attribute values of selected tags.
 *
 * <p>
 * Parser instances should be generally one-time use only - since style sheets are parsed rather rarely (usually once
 * per application run), reusing LSS parsers usually does not make much sense.
 *
 * @author MJ */
public class LssParser {
    private final LmlParser parser;
    private final LmlStyleSheet styleSheet;
    private final LmlTemplateReader reader;
    private final char inheritanceMarker;
    private final char blockOpening;
    private final char blockClosing;
    private final char separator;
    private final char lineEnd;
    private final char tagSeparator;
    private final char commentMarker;
    private final char commentSecondary;

    // Control variables:
    private final StringBuilder builder = new StringBuilder();
    private final Array<String> tags = GdxArrays.newArray();
    private final Array<String> inherits = GdxArrays.newArray();
    private String attribute;
    private final ObjectMap<String, String> attributes = GdxMaps.newObjectMap();

    /** @param parser will be used to extract style sheet and syntax data. */
    public LssParser(final LmlParser parser) {
        this.parser = parser;
        styleSheet = parser.getStyleSheet();
        reader = new DefaultLmlTemplateReader();
        final LssSyntax syntax = parser.getSyntax().getLssSyntax();
        inheritanceMarker = syntax.getInheritanceMarker();
        blockOpening = syntax.getBlockOpening();
        blockClosing = syntax.getBlockClosing();
        separator = syntax.getSeparator();
        lineEnd = syntax.getLineEnd();
        tagSeparator = syntax.getTagSeparator();
        commentMarker = syntax.getCommentMarker();
        commentSecondary = syntax.getSecondaryCommentMarker();
    }

    /** @param lss LML style sheet data. Will be parsed and processed. */
    public void parse(final String lss) {
        reader.append(lss, "LML style sheet");
        try {
            while (reader.hasNextCharacter()) {
                burnWhitespaces();
                if (!reader.hasNextCharacter()) {
                    break;
                }
                parseNames();
                parseAttributes();
                processAttributes();
                tags.clear();
                inherits.clear();
                attributes.clear();
            }
        } finally {
            reader.clear();
        }
    }

    /** @param string will become the exception message. */
    protected void throwException(final String string) {
        parser.throwError(string);
    }

    /** Parses names proceeding styles block. */
    protected void parseNames() {
        while (reader.hasNextCharacter()) {
            final char character = next();
            if (Strings.isWhitespace(character)) {
                addName();
                continue;
            } else if (character == blockOpening) {
                addName();
                break;
            } else {
                builder.append(character);
            }
        }
        if (GdxArrays.isEmpty(tags)) {
            throwException("No tag names chosen.");
        }
    }

    /** Appends tag or inheritance name from the current builder data. */
    protected void addName() {
        if (Strings.isNotEmpty(builder)) {
            int endOffset = 0;
            if (Strings.endsWith(builder, tagSeparator)) { // Ends with ','.
                endOffset = 1;
            }
            if (Strings.startsWith(builder, inheritanceMarker)) { // Starts with '.'.
                inherits.add(builder.substring(1, builder.length() - endOffset));
            } else {
                tags.add(builder.substring(0, builder.length() - endOffset));
            }
            Strings.clearBuilder(builder);
        }
    }

    /** Parses attributes block. */
    protected void parseAttributes() {
        burnWhitespaces();
        attribute = null;
        while (reader.hasNextCharacter()) {
            char character = reader.peekCharacter();
            if (Strings.isNewLine(character) && (attribute != null || Strings.isNotEmpty(builder))) {
                throwException("Expecting line end marker: '" + lineEnd + "'.");
            }
            character = next();
            if (Strings.isNewLine(character) && (attribute != null || Strings.isNotEmpty(builder))) {
                // Needs double check, possible comment along the way.
                throwException("Expecting line end marker: '" + lineEnd + "'.");
            } else if (character == blockClosing) {
                if (attribute != null || Strings.isNotEmpty(builder)) {
                    throwException("Unexpected tag close.");
                }
                return;
            } else if (Strings.isWhitespace(character) && attribute == null) {
                continue;
            } else if (character == separator && attribute == null) {
                addAttributeName();
                continue;
            } else if (character == lineEnd) {
                if (attribute == null) {
                    throwException("Found unexpected line end marker: '" + lineEnd + "'. Is separator (" + separator
                            + ") missing?");
                }
                addAttribute();
            } else {
                builder.append(character);
            }
        }
    }

    /** Caches currently parsed attribute name. */
    protected void addAttributeName() {
        if (Strings.isNotEmpty(builder)) {
            attribute = builder.toString();
            Strings.clearBuilder(builder);
        }
    }

    /** Clears attribute cache, adds default attribute value. */
    protected void addAttribute() {
        attributes.put(attribute, builder.toString().trim());
        attribute = null;
        Strings.clearBuilder(builder);
    }

    /** Adds the stored attribute values to the style sheet. Resolves inherited styles. */
    protected void processAttributes() {
        for (final String tag : tags) {
            for (final String inherit : inherits) {
                styleSheet.addStyles(tag, styleSheet.getStyles(inherit));
            }
            styleSheet.addStyles(tag, attributes);
        }
    }

    /** Analyzes characters, raising the index. Stops after encountering first non-whitespace character. */
    protected void burnWhitespaces() {
        while (reader.hasNextCharacter()) {
            final char character = reader.peekCharacter();
            if (Strings.isWhitespace(character) || character == commentMarker && reader.hasNextCharacter(1)
                    && reader.peekCharacter(1) == commentSecondary) {
                next();
            } else {
                return;
            }
        }
    }

    /** @return next comment in the style sheet, with comments removed. */
    protected char next() {
        final char next = reader.nextCharacter();
        if (next == commentMarker && reader.hasNextCharacter() && reader.peekCharacter() == commentSecondary) {
            reader.nextCharacter();
            while (reader.hasNextCharacter()) {
                final char character = reader.nextCharacter();
                if (character == commentSecondary && reader.hasNextCharacter()
                        && reader.peekCharacter() == commentMarker) {
                    reader.nextCharacter();
                    return reader.hasNextCharacter() ? next() : '\n';
                }
            }
        }
        return next;
    }

    /** Clears control variables. */
    public void reset() {
        attribute = null;
        tags.clear();
        inherits.clear();
        attributes.clear();
        Strings.clearBuilder(builder);
    }
}
