package com.github.czyzby.lml.vis.parser.impl.attribute.building;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.tag.builder.VisWindowLmlActorBuilder;
import com.kotcrab.vis.ui.widget.VisWindow;

/** See {@link VisWindow#VisWindow(String, boolean)}. Mapped to "showBorder", "showWindowBorder".
 *
 * @author MJ */
public class ShowWindowBorderLmlAttribute implements LmlBuildingAttribute<VisWindowLmlActorBuilder> {
    @Override
    public Class<VisWindowLmlActorBuilder> getBuilderType() {
        return VisWindowLmlActorBuilder.class;
    }

    @Override
    public boolean process(final LmlParser parser, final LmlTag tag, final VisWindowLmlActorBuilder builder,
            final String rawAttributeData) {
        builder.setShowWindowBorder(parser.parseBoolean(rawAttributeData));
        return FULLY_PARSED;
    }
}
