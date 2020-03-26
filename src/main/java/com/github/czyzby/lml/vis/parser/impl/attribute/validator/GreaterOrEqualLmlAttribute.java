package com.github.czyzby.lml.vis.parser.impl.attribute.validator;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;

/** See {@link GreaterThanValidator#setUseEquals(boolean)}. Mapped to "orEqual", "allowEqual", "greaterOrEqual".
 *
 * @author MJ */
public class GreaterOrEqualLmlAttribute implements LmlAttribute<GreaterThanValidator> {
    @Override
    public Class<GreaterThanValidator> getHandledType() {
        return GreaterThanValidator.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final GreaterThanValidator actor,
            final String rawAttributeData) {
        actor.setUseEquals(parser.parseBoolean(rawAttributeData, tag.getParent().getActor()));
    }
}
