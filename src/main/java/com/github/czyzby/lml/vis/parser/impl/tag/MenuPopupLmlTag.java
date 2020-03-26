package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.Separator;

/** Handles {@link PopupMenu} actors. Can be attached only to {@link MenuItemLmlTag}s as submenus. Mapped to
 * "popupMenu", "subMenu".
 *
 * @author MJ */
public class MenuPopupLmlTag extends AbstractActorLmlTag { // Name is inverted to keep the menu tags "family" together.
    public MenuPopupLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new PopupMenu(builder.getStyleName());
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        addMenuItem(new MenuItem(getParser().parseString(plainTextLine, getActor())));
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        if (childTag.getActor() instanceof MenuItem) {
            addMenuItem((MenuItem) childTag.getActor());
        } else if (childTag.getActor() instanceof Separator) {
            addSeparator((Separator) childTag.getActor());
        } else {
            getParser().throwErrorIfStrict("Menus can handle only menu item and separator children. Found child tag: "
                    + childTag.getTagName() + " with actor: " + childTag.getActor());
        }
    }

    /** @param menuItem will be added to the menu. Cannot be null. */
    protected void addMenuItem(final MenuItem menuItem) {
        getPopupMenu().addItem(menuItem);
    }

    /** @param separator will be added to the menu. Should not be null. */
    protected void addSeparator(final Separator separator) {
        // This is basically a copy of #addSeparator() method in PopupMenu:
        getPopupMenu().add(separator).padTop(2).padBottom(2).fill().expand().row();
    }

    /** @return casted actor. */
    protected PopupMenu getPopupMenu() {
        return (PopupMenu) getActor();
    }

    @Override
    public boolean isAttachable() {
        return true;
    }

    @Override
    public void attachTo(final LmlTag tag) {
        if (tag.getActor() instanceof MenuItem) {
            ((MenuItem) tag.getActor()).setSubMenu(getPopupMenu());
        } else {
            // Technically, these menus could be attached to any widget with on-click listener, but let's leave that for
            // now - this would require a custom listener + possibly a way to modify it.
            getParser().throwError(
                    "Popup menus can currently be attached only to MenuItems as sub-menus. Popup menu was a child of: "
                            + tag.getTagName());
        }
    }
}
