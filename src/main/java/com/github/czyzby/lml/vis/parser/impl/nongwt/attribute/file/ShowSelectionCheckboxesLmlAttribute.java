package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file;

import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.file.FileChooser;

/** See {@link FileChooser#setShowSelectionCheckboxes(boolean)}. Expects a boolean. Mapped to "showSelectionCheckboxes".
 * @author MJ */
public class ShowSelectionCheckboxesLmlAttribute implements LmlAttribute<FileChooser> {
    @Override
    public Class<FileChooser> getHandledType() {
        return FileChooser.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final FileChooser actor,
            final String rawAttributeData) {
        actor.setShowSelectionCheckboxes(parser.parseBoolean(rawAttributeData, actor));
    }
}
