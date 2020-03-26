package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;

/** Handles {@link Menu} widget. Typically used inside a {@link MenuBarLmlTag}. Although technically Menu extends Table
 * class, cell attributes CANNOT be present in its children's tags, as Menus have custom children appending methods.
 * Converts text lines to {@link MenuItem}s with default style. Can handle only {@link MenuItemLmlTag} children. Mapped
 * to "menu".
 *
 * @author MJ */
public class MenuLmlTag extends MenuPopupLmlTag {
    public MenuLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected LmlActorBuilder getNewInstanceOfBuilder() {
        return new TextLmlActorBuilder();
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new Menu(((TextLmlActorBuilder) builder).getText(), builder.getStyleName());
    }

    @Override
    public boolean isAttachable() {
        return false;
    }

    @Override
    public void attachTo(final LmlTag tag) {
        // Only sub-menus are attachable. Throwing exception if anyone tries to attach a regular menu.
        getParser().throwError("Regular menus are not attachable.");
    }
}
