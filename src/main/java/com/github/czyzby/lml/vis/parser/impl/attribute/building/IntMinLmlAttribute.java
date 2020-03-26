package com.github.czyzby.lml.vis.parser.impl.attribute.building;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.tag.builder.IntRangeLmlActorBuilder;

/** Sets the start of an int range. Mapped to "min".
 *
 * @author MJ */
public class IntMinLmlAttribute implements LmlBuildingAttribute<IntRangeLmlActorBuilder> {
    @Override
    public Class<IntRangeLmlActorBuilder> getBuilderType() {
        return IntRangeLmlActorBuilder.class;
    }

    @Override
    public boolean process(final LmlParser parser, final LmlTag tag, final IntRangeLmlActorBuilder builder,
            final String rawAttributeData) {
        builder.setMin(parser.parseInt(rawAttributeData));
        return FULLY_PARSED;
    }
}
