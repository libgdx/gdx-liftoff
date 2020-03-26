package com.github.czyzby.lml.parser.impl.attribute.building;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.FloatRangeLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Used to parse step size of numeric range widgets. This is the minimum value that can be added or subtracted from the
 * current range value. Expects a float. Mapped to "stepSize", "step".
 *
 * @author MJ */
public class RangeStepSizeLmlAttribute implements LmlBuildingAttribute<FloatRangeLmlActorBuilder> {
    @Override
    public Class<FloatRangeLmlActorBuilder> getBuilderType() {
        return FloatRangeLmlActorBuilder.class;
    }

    @Override
    public boolean process(final LmlParser parser, final LmlTag tag, final FloatRangeLmlActorBuilder builder,
            final String rawAttributeData) {
        builder.setStepSize(parser.parseFloat(rawAttributeData));
        return FULLY_PARSED;
    }
}
