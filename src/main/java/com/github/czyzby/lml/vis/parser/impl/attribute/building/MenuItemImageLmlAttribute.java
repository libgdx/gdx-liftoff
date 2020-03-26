package com.github.czyzby.lml.vis.parser.impl.attribute.building;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.tag.builder.MenuItemLmlActorBuilder;

/** See {@link MenuItemLmlActorBuilder#setImage(String)}. Sets the name of a drawable that will be used as menu item's
 * icon. Mapped to "icon", "image", "drawable".
 *
 * @author MJ */
public class MenuItemImageLmlAttribute implements LmlBuildingAttribute<MenuItemLmlActorBuilder> {
    @Override
    public Class<MenuItemLmlActorBuilder> getBuilderType() {
        return MenuItemLmlActorBuilder.class;
    }

    @Override
    public boolean process(final LmlParser parser, final LmlTag tag, final MenuItemLmlActorBuilder builder,
            final String rawAttributeData) {
        builder.setImage(parser.parseString(rawAttributeData));
        return FULLY_PARSED;
    }
}
