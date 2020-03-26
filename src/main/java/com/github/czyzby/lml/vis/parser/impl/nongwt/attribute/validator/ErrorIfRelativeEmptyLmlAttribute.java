package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.validator;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.util.form.FormValidator.FileExistsValidator;

/** See {@link FileExistsValidator#setErrorIfRelativeEmpty(boolean)}. Mapped to "errorIfRelativeEmpty".
 *
 * @author MJ */
public class ErrorIfRelativeEmptyLmlAttribute implements LmlAttribute<FileExistsValidator> {
    @Override
    public Class<FileExistsValidator> getHandledType() {
        return FileExistsValidator.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final FileExistsValidator actor,
            final String rawAttributeData) {
        actor.setErrorIfRelativeEmpty(parser.parseBoolean(rawAttributeData, actor));
    }
}
