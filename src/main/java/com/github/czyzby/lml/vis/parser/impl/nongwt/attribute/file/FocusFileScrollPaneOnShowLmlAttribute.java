package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.file.FileChooser;

/**
 * See {@link FileChooser#focusFileScrollPaneOnShow}. Expects a boolean. Mapped to "focusFileScrollPaneOnShow". Note
 * that this setting is static (global).
 * @author MJ
 */
public class FocusFileScrollPaneOnShowLmlAttribute implements LmlAttribute<FileChooser> {
    @Override
    public Class<FileChooser> getHandledType() {
        return FileChooser.class;
    }

    @Override
    public void process(LmlParser parser, LmlTag tag, FileChooser fileChooser, String rawAttributeData) {
        FileChooser.focusFileScrollPaneOnShow = parser.parseBoolean(rawAttributeData, fileChooser);
    }
}
