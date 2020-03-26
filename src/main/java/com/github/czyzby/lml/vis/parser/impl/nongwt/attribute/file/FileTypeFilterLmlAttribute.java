package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;

/** See {@link FileChooser#setFileTypeFilter(FileTypeFilter)}. Expects an action ID of a method that returns
 * {@link FileTypeFilter} instance. Mapped to "fileTypeFilter".
 *
 * @author MJ */
public class FileTypeFilterLmlAttribute implements LmlAttribute<FileChooser> {
    @Override
    public Class<FileChooser> getHandledType() {
        return FileChooser.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final FileChooser actor,
            final String rawAttributeData) {
        final ActorConsumer<?, FileChooser> action = parser.parseAction(rawAttributeData, actor);
        if (action == null) {
            parser.throwErrorIfStrict(
                    "File type filter attribute expects an action that returns a FileTypeFilter instance. Action not found for: "
                            + rawAttributeData);
        }
        final Object result = action.consume(actor);
        if (result instanceof FileTypeFilter) {
            actor.setFileTypeFilter((FileTypeFilter) result);
        } else {
            parser.throwErrorIfStrict(
                    "Unable to set file type filter. A method returning FileTypeFilter instance is required; got result: "
                            + result);
        }
    }

}
