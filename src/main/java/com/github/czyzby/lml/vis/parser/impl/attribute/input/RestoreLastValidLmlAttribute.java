package com.github.czyzby.lml.vis.parser.impl.attribute.input;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/** See {@link VisValidatableTextField#setRestoreLastValid(boolean)}. Mapped to "restore", "restoreLastValid".
 *
 * @author MJ */
public class RestoreLastValidLmlAttribute implements LmlAttribute<VisValidatableTextField> {
    @Override
    public Class<VisValidatableTextField> getHandledType() {
        return VisValidatableTextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisValidatableTextField actor,
            final String rawAttributeData) {
        actor.setRestoreLastValid(parser.parseBoolean(rawAttributeData, actor));
    }
}
