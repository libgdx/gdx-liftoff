package com.github.czyzby.lml.parser.impl.attribute.building;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Chooses widget's Skin name use to get its style. Expects a string. If attribute is not present, default skin is
 * used. By default, mapped to "skin" attribute names.
 *
 * @author MJ */
public class SkinLmlAttribute implements LmlBuildingAttribute<LmlActorBuilder> {
    @Override
    public Class<LmlActorBuilder> getBuilderType() {
        return LmlActorBuilder.class;
    }

    @Override
    public boolean process(final LmlParser parser, final LmlTag tag, final LmlActorBuilder builder,
            final String rawAttributeData) {
        builder.setSkinName(parser.parseString(rawAttributeData));
        return FULLY_PARSED;
    }
}
