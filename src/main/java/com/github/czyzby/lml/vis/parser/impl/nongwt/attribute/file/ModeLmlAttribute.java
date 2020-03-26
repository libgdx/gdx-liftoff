package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file;

import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;

/** See {@link FileChooser#setMode(Mode)}. Expects a string which matches a {@link Mode} enum constant ignoring case.
 * Mapped to "mode".
 *
 * @author MJ */
public class ModeLmlAttribute implements LmlAttribute<FileChooser> {
    @Override
    public Class<FileChooser> getHandledType() {
        return FileChooser.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final FileChooser actor,
            final String rawAttributeData) {
        actor.setMode(determineMode(parser, actor, rawAttributeData));
    }

    /** @param parser parses the tag.
     * @param actor needs a selection mode.
     * @param rawAttributeData unparsed attribute's value.
     * @return chosen {@link Mode}. Throws exception if ID not valid and parser is strict; returns default value if
     *         parser not strict. */
    protected Mode determineMode(final LmlParser parser, final FileChooser actor, final String rawAttributeData) {
        final String modeName = Strings.toUpperCase(parser.parseString(rawAttributeData, actor));
        try {
            return Mode.valueOf(modeName);
        } catch (final Exception exception) {
            // Unknown ID.
            parser.throwErrorIfStrict("Unable to determine mode with data: " + rawAttributeData, exception);
        }
        return Mode.OPEN;
    }
}