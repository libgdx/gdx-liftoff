package com.github.czyzby.lml.vis.parser.impl.tag.builder;

import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;

/** Allows to create {@link com.kotcrab.vis.ui.widget.MenuItem} widgets. Additionally to setting initial text, allows to
 * choose a menu item icon.
 *
 * @author MJ */
public class MenuItemLmlActorBuilder extends TextLmlActorBuilder {
    private String image;

    /** @return name of the drawable that should be used to create icon of the menu item. */
    public String getImage() {
        return image;
    }

    /** @param image becomes the name of the drawable that should be used to create icon of the menu item. */
    public void setImage(final String image) {
        this.image = image;
    }
}
