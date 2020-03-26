package com.github.czyzby.lml.parser.impl.attribute.building;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.FloatRangeLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Used to parse min value of numeric range widgets. Expects a float. Mapped to "min".
 *
 * @author MJ */
public class RangeMinValueLmlAttribute implements LmlBuildingAttribute<FloatRangeLmlActorBuilder> {
    @Override
    public Class<FloatRangeLmlActorBuilder> getBuilderType() {
        return FloatRangeLmlActorBuilder.class;
    }

    @Override
    public boolean process(final LmlParser parser, final LmlTag tag, final FloatRangeLmlActorBuilder builder,
            final String rawAttributeData) {
        builder.setMin(parser.parseFloat(rawAttributeData));
        return FULLY_PARSED;
    }
}
