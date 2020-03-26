package com.github.czyzby.lml.parser.impl.attribute.building;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TooltipLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Allows to specify tooltip manager used to create and manage a tooltip. Expects a string - ID of a manager registered
 * in LML data object. Mapped to "tooltipManager".
 *
 * @author MJ */
public class TooltipManagerLmlAttribute implements LmlBuildingAttribute<TooltipLmlActorBuilder> {
    @Override
    public Class<TooltipLmlActorBuilder> getBuilderType() {
        return TooltipLmlActorBuilder.class;
    }

    @Override
    public boolean process(final LmlParser parser, final LmlTag tag, final TooltipLmlActorBuilder builder,
            final String rawAttributeData) {
        builder.setTooltipManager(parser.parseString(rawAttributeData));
        return FULLY_PARSED;
    }
}
