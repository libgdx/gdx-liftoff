package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.file.FileChooser;

/** See {@link FileChooser#setFileDeleter(FileChooser.FileDeleter)}. Expects an ID of an
 * action that returns a FileDeleter instance. Mapped to "fileDeleter".
 *
 * @author MJ */
public class FileDeleterLmlAttribute implements LmlAttribute<FileChooser> {
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
                    "File deleter attribute expects an action that returns a FileDeleter instance. Action not found for: "
                            + rawAttributeData);
        }
        final Object result = action.consume(actor);
        if (result instanceof FileChooser.FileDeleter) {
            actor.setFileDeleter((FileChooser.FileDeleter) result);
        } else {
            parser.throwErrorIfStrict(
                    "Unable to set file deleter. A method returning FileDeleter instance is required; got result: "
                            + result);
        }
    }
}
