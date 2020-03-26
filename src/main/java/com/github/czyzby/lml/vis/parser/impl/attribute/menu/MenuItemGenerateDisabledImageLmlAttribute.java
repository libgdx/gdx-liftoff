package com.github.czyzby.lml.vis.parser.impl.attribute.menu;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.MenuItem;

/** See {@link MenuItem#setGenerateDisabledImage(boolean)}. Mapped to "generateDisabled".
 *
 * @author MJ */
public class MenuItemGenerateDisabledImageLmlAttribute implements LmlAttribute<MenuItem> {
    @Override
    public Class<MenuItem> getHandledType() {
        return MenuItem.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final MenuItem actor, final String rawAttributeData) {
        actor.setGenerateDisabledImage(parser.parseBoolean(rawAttributeData, actor));
    }
}
