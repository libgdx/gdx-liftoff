package com.github.czyzby.lml.vis.parser.impl.attribute.validator;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;

/** See {@link GreaterThanValidator#setGreaterThan(float)}. Mapped to "value", "min", "greaterThan".
 *
 * @author MJ */
public class GreaterThanLmlAttribute implements LmlAttribute<GreaterThanValidator> {
    @Override
    public Class<GreaterThanValidator> getHandledType() {
        return GreaterThanValidator.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final GreaterThanValidator actor,
            final String rawAttributeData) {
        actor.setGreaterThan(parser.parseFloat(rawAttributeData, tag.getParent().getActor()));
    }
}
