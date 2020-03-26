package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.file.FileChooser;

/**
 * See {@link FileChooser#setSortingOrderAscending(boolean)}. Expects a boolean. Mapped to "sortingOrderAscending".
 * @author MJ
 */
public class FileSortingOrderLmlAttribute implements LmlAttribute<FileChooser> {
    @Override
    public Class<FileChooser> getHandledType() {
        return FileChooser.class;
    }

    @Override
    public void process(LmlParser parser, LmlTag tag, FileChooser fileChooser, String rawAttributeData) {
        fileChooser.setSortingOrderAscending(parser.parseBoolean(rawAttributeData, fileChooser));
    }
}
