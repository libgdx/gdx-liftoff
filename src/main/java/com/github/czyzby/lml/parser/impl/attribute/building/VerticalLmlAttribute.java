package com.github.czyzby.lml.parser.impl.attribute.building;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.AlignedLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Allows to build widgets that need the horizontal or vertical setting in the constructor (especially since their
 * default style varies according to this setting). Expects a boolean. By default, mapped to "vertical".
 *
 * @author MJ */
public class VerticalLmlAttribute implements LmlBuildingAttribute<AlignedLmlActorBuilder> {
    @Override
    public Class<AlignedLmlActorBuilder> getBuilderType() {
        return AlignedLmlActorBuilder.class;
    }

    @Override
    public boolean process(final LmlParser parser, final LmlTag tag, final AlignedLmlActorBuilder builder,
            final String rawAttributeData) {
        builder.setVertical(parser.parseBoolean(rawAttributeData));
        return FULLY_PARSED;
    }
}
