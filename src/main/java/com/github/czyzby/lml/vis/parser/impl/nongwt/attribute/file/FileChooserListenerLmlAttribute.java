package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.FileChooserListener;

/** See {@link FileChooser#setListener(FileChooserListener)}. Attribute expects a reference to a method that consumes a
 * single {@link FileHandle} or an {@link Array} of files. If the file chooser is cancelled, file handle-consuming
 * method will receive null; array-consuming method will receive empty array. Mapped to "listener",
 * "fileChooserListener".
 *
 * @author MJ */
public class FileChooserListenerLmlAttribute implements LmlAttribute<FileChooser> {
    @Override
    public Class<FileChooser> getHandledType() {
        return FileChooser.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final FileChooser actor,
            final String rawAttributeData) {
        addFileChooserListener(parser, rawAttributeData, actor);
    }

    /** @param parser parses the attribute.
     * @param rawAttributeData attribute value.
     * @param fileChooser will have a listener set. */
    protected static void addFileChooserListener(final LmlParser parser, final String rawAttributeData,
            final FileChooser fileChooser) {
        final ActorConsumer<?, FileHandle> action = parser.parseAction(rawAttributeData, MockUpFileHandle.INSTANCE);
        if (action != null) {
            // Handling single file:
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setListener(new FileChooserAdapter() {
                @Override
                public void selected(final Array<FileHandle> file) {
                    if (GdxArrays.isNotEmpty(file)) {
                        action.consume(file.first());
                    }
                }

                @Override
                public void canceled() {
                    action.consume(null);
                }
            });
        } else {
            // Handling multiple files:
            final ActorConsumer<?, Array<FileHandle>> directoryAction = parser.parseAction(rawAttributeData,
                    MockUpFileHandle.EMPTY_ARRAY);
            if (directoryAction == null) {
                parser.throwError(
                        "File chooser attribute needs a reference to an action consuming a FileHandle or Array<FileHandle>. Action not found for: "
                                + rawAttributeData);
            }
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setListener(new FileChooserListener() {
                @Override
                public void selected(final Array<FileHandle> files) {
                    directoryAction.consume(files);
                }

                @Override
                public void canceled() {
                    directoryAction.consume(GdxArrays.newArray(FileHandle.class));
                }
            });
        }
    }
}
