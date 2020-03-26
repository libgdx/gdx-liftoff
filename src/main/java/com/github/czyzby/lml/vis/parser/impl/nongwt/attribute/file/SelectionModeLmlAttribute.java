package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file;

import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;

/** See {@link FileChooser#setSelectionMode(SelectionMode)}. Expects a string which matches a {@link SelectionMode} enum
 * constant ignoring case. Mapped to "selectionMode", "select".
 *
 * @author MJ */
public class SelectionModeLmlAttribute implements LmlAttribute<FileChooser> {
    @Override
    public Class<FileChooser> getHandledType() {
        return FileChooser.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final FileChooser actor,
            final String rawAttributeData) {
        actor.setSelectionMode(determineMode(parser, actor, rawAttributeData));
    }

    /** @param parser parses the tag.
     * @param actor needs a selection mode.
     * @param rawAttributeData unparsed attribute's value.
     * @return chosen {@link SelectionMode}. Throws exception if ID not valid and parser is strict; returns default
     *         value if parser not strict. */
    protected SelectionMode determineMode(final LmlParser parser, final FileChooser actor,
            final String rawAttributeData) {
        final String modeName = Strings.toUpperCase(parser.parseString(rawAttributeData, actor).trim());
        try {
            return SelectionMode.valueOf(modeName);
        } catch (final Exception exception) {
            // Unknown ID.
            parser.throwErrorIfStrict("Unable to determine selection mode with data: " + rawAttributeData, exception);
        }
        return SelectionMode.FILES;
    }
}
