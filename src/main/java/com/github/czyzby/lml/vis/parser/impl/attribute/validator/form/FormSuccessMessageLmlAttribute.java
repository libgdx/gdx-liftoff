package com.github.czyzby.lml.vis.parser.impl.attribute.validator.form;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.ui.VisFormTable;

/** See {@link VisFormTable#setSuccessMessage(String)}. Will be displayed if there are no errors in wrapped form. Mapped
 * to "success", "successMsg", "successMessage".
 *
 * @author MJ */
public class FormSuccessMessageLmlAttribute implements LmlAttribute<VisFormTable> {
    @Override
    public Class<VisFormTable> getHandledType() {
        return VisFormTable.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisFormTable actor,
            final String rawAttributeData) {
        actor.setSuccessMessage(parser.parseString(rawAttributeData, actor));
    }
}
