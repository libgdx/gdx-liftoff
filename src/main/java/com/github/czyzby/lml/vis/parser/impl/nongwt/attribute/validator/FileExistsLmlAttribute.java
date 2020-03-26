package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.validator;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.util.form.FormValidator.FileExistsValidator;

/** See {@link FileExistsValidator#setMustNotExist(boolean)}. Attribute is inverted (relatively to method) for
 * simplified name. If attribute value is set to true, file is required to exist. If false, file cannot exist. Mapped to
 * "exists".
 *
 * @author MJ */
public class FileExistsLmlAttribute implements LmlAttribute<FileExistsValidator> {
    @Override
    public Class<FileExistsValidator> getHandledType() {
        return FileExistsValidator.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final FileExistsValidator actor,
            final String rawAttributeData) {
        actor.setMustNotExist(!parser.parseBoolean(rawAttributeData, actor));
    }
}
