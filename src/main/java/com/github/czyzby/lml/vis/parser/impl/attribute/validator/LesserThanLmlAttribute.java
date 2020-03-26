package com.github.czyzby.lml.vis.parser.impl.attribute.validator;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.util.Validators.LesserThanValidator;

/** See {@link LesserThanValidator#setLesserThan(float)}. Mapped to "value", "max", "lesserThan".
 *
 * @author MJ */
public class LesserThanLmlAttribute implements LmlAttribute<LesserThanValidator> {
    @Override
    public Class<LesserThanValidator> getHandledType() {
        return LesserThanValidator.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final LesserThanValidator actor,
            final String rawAttributeData) {
        actor.setLesserThan(parser.parseFloat(rawAttributeData, tag.getParent().getActor()));
    }
}
