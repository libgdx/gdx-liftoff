package com.github.czyzby.lml.vis.parser.impl.tag;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Handles {@link com.kotcrab.vis.ui.widget.Separator} widgets inside menu tags. Overrides default style name with a
 * custom one, designed for menus. Basically, this is a menu utility. Mapped to "menuSeparator".
 *
 * @author MJ */
public class MenuSeparatorLmlTag extends SeparatorLmlTag {
    public MenuSeparatorLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected LmlActorBuilder getNewInstanceOfBuilder() {
        final LmlActorBuilder builder = super.getNewInstanceOfBuilder();
        builder.setStyleName("menu"); // This is the default menu separator style.
        return builder;
    }
}
