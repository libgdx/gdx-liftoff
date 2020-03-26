package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file;

import java.io.File;
import java.io.FileFilter;

import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.file.FileChooser;

/** See {@link FileChooser#setFileFilter(FileFilter)}. Expects an action ID that references a method consuming
 * {@link File} instance and returning a boolean (boxed or unboxed). Method will be invoked each time a file is
 * filtered. Mapped to "fileFilter".
 *
 * @author MJ */
public class FileFilterLmlAttribute implements LmlAttribute<FileChooser> {
    @Override
    public Class<FileChooser> getHandledType() {
        return FileChooser.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final FileChooser actor,
            final String rawAttributeData) {
        @SuppressWarnings("unchecked") final ActorConsumer<Boolean, File> filter = (ActorConsumer<Boolean, File>) parser
                .parseAction(rawAttributeData, new File(Strings.EMPTY_STRING));
        if (filter == null) {
            parser.throwErrorIfStrict(
                    "File filter attribute expects a method that consumes a File and returns boolean/Boolean. Method not found for ID: "
                            + rawAttributeData);
            return;
        }
        actor.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(final File pathname) {
                return filter.consume(pathname);
            }
        });
    }
}
