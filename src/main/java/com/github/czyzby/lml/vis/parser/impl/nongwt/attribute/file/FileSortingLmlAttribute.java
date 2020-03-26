package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file;

import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.file.FileChooser;

/**
 * See {@link FileChooser#setSorting(FileChooser.FileSorting)}. Expects a name of {@link FileChooser.FileSorting} enum
 * instance. Mapped to "sorting".
 * @author MJ
 */
public class FileSortingLmlAttribute implements LmlAttribute<FileChooser> {
    @Override
    public Class<FileChooser> getHandledType() {
        return FileChooser.class;
    }

    @Override
    public void process(LmlParser parser, LmlTag tag, FileChooser fileChooser, String rawAttributeData) {
        fileChooser.setSorting(determineSorting(parser, fileChooser, rawAttributeData));
    }

    /**
     * @param parser parses the tag.
     * @param actor needs a sorting mode.
     * @param rawAttributeData unparsed attribute's value.
     * @return chosen {@link FileChooser.FileSorting}. Throws exception if ID not valid and parser is strict; returns
     * default value if parser not strict.
     */
    protected FileChooser.FileSorting determineSorting(final LmlParser parser, final FileChooser actor,
            final String rawAttributeData) {
        final String modeName = Strings.toUpperCase(parser.parseString(rawAttributeData, actor).trim());
        try {
            return FileChooser.FileSorting.valueOf(modeName);
        } catch (final Exception exception) {
            // Unknown ID.
            parser.throwErrorIfStrict("Unable to determine sorting mode with data: " + rawAttributeData, exception);
        }
        return FileChooser.FileSorting.NAME;
    }
}
