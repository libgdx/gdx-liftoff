package com.github.czyzby.lml.parser.impl.attribute.building;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Sets initial text of text-based widgets. Used to construct the widgets. Expects a string. By default, mapped to
 * "text" and "value" attribute names.
 *
 * @author MJ */
public class TextLmlAttribute implements LmlBuildingAttribute<TextLmlActorBuilder> {
    @Override
    public Class<TextLmlActorBuilder> getBuilderType() {
        return TextLmlActorBuilder.class;
    }

    @Override
    public boolean process(final LmlParser parser, final LmlTag tag, final TextLmlActorBuilder builder,
            final String rawAttributeData) {
        builder.setText(parser.parseString(rawAttributeData));
        return FULLY_PARSED;
    }
}
