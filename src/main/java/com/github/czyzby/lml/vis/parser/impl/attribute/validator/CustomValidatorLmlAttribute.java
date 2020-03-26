package com.github.czyzby.lml.vis.parser.impl.attribute.validator;

import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.tag.validator.CustomValidatorLmlTag.CustomValidator;

/** Allows to set a custom method wrapped by a form validator. Expects an action ID that references a method consuming
 * String and returning boolean (boxed or unboxed). Mapped to "validator", "validate", "method", "action", "check".
 *
 * @author MJ */
public class CustomValidatorLmlAttribute implements LmlAttribute<CustomValidator> {
    @Override
    public Class<CustomValidator> getHandledType() {
        return CustomValidator.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final CustomValidator actor,
            final String rawAttributeData) {
        @SuppressWarnings("unchecked") final ActorConsumer<Boolean, String> validator = (ActorConsumer<Boolean, String>) parser
                .parseAction(rawAttributeData, Strings.EMPTY_STRING);
        if (validator == null) {
            parser.throwError("Cannot create custom validator. Invalid method ID: " + rawAttributeData);
        }
        actor.setValidator(validator);
    }
}
