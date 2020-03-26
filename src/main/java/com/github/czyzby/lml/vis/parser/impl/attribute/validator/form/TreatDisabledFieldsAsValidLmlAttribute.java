
package com.github.czyzby.lml.vis.parser.impl.attribute.validator.form;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.ui.VisFormTable;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator;

/** See {@link SimpleFormValidator#setTreatDisabledFieldsAsValid(boolean)}. Mapped to "treatDisabledFieldsAsValid",
 * "disabledValid".
 * 
 * @author MJ */
public class TreatDisabledFieldsAsValidLmlAttribute implements LmlAttribute<VisFormTable> {
    @Override
    public Class<VisFormTable> getHandledType() {
        return VisFormTable.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisFormTable actor,
            final String rawAttributeData) {
        actor.getFormValidator().setTreatDisabledFieldsAsValid(parser.parseBoolean(rawAttributeData, actor));
    }
}
