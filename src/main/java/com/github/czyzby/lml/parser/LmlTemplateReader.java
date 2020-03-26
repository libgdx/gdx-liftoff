package com.github.czyzby.lml.parser;

import com.badlogic.gdx.files.FileHandle;

/** Common interface for readers that queue and parse LML templates. Allows to read template files character by
 * character.
 *
 * @author MJ
 * @see com.github.czyzby.lml.parser.impl.DefaultLmlTemplateReader */
public interface LmlTemplateReader {
    /** Registers template (or a part of it) to the reader. TemplateReader should work as a FIFO queue - if another
     * template is being currently parsed and one of append() methods is used, the previous template will be queued and
     * parsed AFTER the newer text. This allows, for example, to include separate files from within the templates by
     * using macros.
     *
     * @param template will become currently parsed part of the template. */
    void append(char[] template);

    /** Registers template (or a part of it) to the reader. TemplateReader should work as a FIFO queue - if another
     * template is being currently parsed and one of append() methods is used, the previous template will be queued and
     * parsed AFTER the newer text. This allows, for example, to include separate files from within the templates by
     * using macros.
     *
     * @param template will become currently parsed part of the template. */
    void append(CharSequence template);

    /** Registers template (or a part of it) to the reader. TemplateReader should work as a FIFO queue - if another
     * template is being currently parsed and one of append() methods is used, the previous template will be queued and
     * parsed AFTER the newer text. This allows, for example, to include separate files from within the templates by
     * using macros.
     *
     * @param template will become currently parsed part of the template.
     * @param templateName can be used to identify the template */
    void append(CharSequence template, String templateName);

    /** Registers template (or a part of it) to the reader. TemplateReader should work as a FIFO queue - if another
     * template is being currently parsed and one of append() methods is used, the previous template will be queued and
     * parsed AFTER the newer text. This allows, for example, to include separate files from within the templates by
     * using macros.
     *
     * @param template will become currently parsed part of the template. */
    void append(String template);

    /** Registers template (or a part of it) to the reader. TemplateReader should work as a FIFO queue - if another
     * template is being currently parsed and one of append() methods is used, the previous template will be queued and
     * parsed AFTER the newer text. This allows, for example, to include separate files from within the templates by
     * using macros.
     *
     * @param template will become currently parsed part of the template.
     * @param templateName can be used to identify the template. */
    void append(String template, String templateName);

    /** Registers template (or a part of it) to the reader. TemplateReader should work as a FIFO queue - if another
     * template is being currently parsed and one of append() methods is used, the previous template will be queued and
     * parsed AFTER the newer text. This allows, for example, to include separate files from within the templates by
     * using macros.
     *
     * @param templateFile will be read and become currently parsed part of the template. */
    void append(FileHandle templateFile);

    /** @return true if currently parsed template has more characters. */
    boolean hasNextCharacter();

    /** @param additionalIndexes number of indexes that should be skipped to check if the character exists. For example,
     *            if we're currently on 4th character of a char sequence, passing 3 to this method will check if 7th
     *            character exists. Passing 0 returns same value as {@link #hasNextCharacter()}.
     * @return true if the character with modified index exists. */
    boolean hasNextCharacter(int additionalIndexes);

    /** @return next character in the currently parsed template.
     * @throws RuntimeException if has no characters left. */
    char nextCharacter();

    /** @return next character in the currently parsed template. Does not change the current character index, so the
     *         next {@link #nextCharacter()} call will still return the same character. This is useful for situations
     *         when multiple characters need to be pre-checked before taking an action.
     * @throws RuntimeException if has no characters left. */
    char peekCharacter();

    /** @param additionalIndexes number of indexes that should be skipped. For example, if we're currently on 4th
     *            character of a char sequence, passing 3 to this method will return 7th character. Passing 0 returns
     *            same value as {@link #peekCharacter()}.
     * @return character with the modified index.
     * @throws RuntimeException if too few characters to peek value if that many additional indexes. */
    char peekCharacter(int additionalIndexes);

    /** @return currently parsed line of the original template. */
    int getCurrentLine();

    /** @return name of the currently parsed file. */
    String getCurrentTemplateName();

    /** @return currently parsed line of the current template. */
    int getCurrentSequenceLine();

    /** @return name of the currently parsed part of template. */
    String getCurrentSequenceName();

    /** @return currently parsed sequence content. Should not be accessed or modified directly: this is for debugging
     *         and error reporting purposes only. */
    String getCurrentSequence();

    /** @return original parsed sequence content. Should not be accessed or modified directly: this is for debugging and
     *         error reporting purposes only. */
    String getOriginalSequence();

    /** Clears template parts queued for reading. */
    void clear();

    /** Utility debugging and error reporting method.
     *
     * @return true if template reader currently parses the original, first template rather than some nested value. */
    boolean isParsingOriginalTemplate();

    /** @param value cannot be empty or null.
     * @return true if currently stored text starts with the passed value. */
    boolean startsWith(CharSequence value);
}
