package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.actor.ButtonLmlTag;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.tag.builder.MenuItemLmlActorBuilder;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.MenuItem.MenuItemStyle;

/** Handles {@link MenuItem} actors. Similarly to image text button tags, menu items can handle both image and label
 * attributes, which are used to modify its internal widgets. Appends plain text between tags to its label. Can have
 * only {@link MenuPopupLmlTag} children. Should generally have a change listener attached. Mapped to "menuItem",
 * "item".
 *
 * @author MJ */
public class MenuItemLmlTag extends ButtonLmlTag {
    public MenuItemLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TextLmlActorBuilder getNewInstanceOfBuilder() {
        return new MenuItemLmlActorBuilder();
    }

    @Override
    protected final Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        final MenuItemLmlActorBuilder menuItemBuilder = (MenuItemLmlActorBuilder) builder;
        final Skin skin = getSkin(builder);
        final MenuItemStyle style = skin.get(builder.getStyleName(), MenuItemStyle.class);
        if (menuItemBuilder.getImage() == null) {
            // No icon specified: passing null image:
            return new MenuItem(menuItemBuilder.getText(), (Image) null, style);
        }
        // Icon specified: passing chosen drawable:
        return new MenuItem(menuItemBuilder.getText(), skin.getDrawable(menuItemBuilder.getImage()), style);
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        getParser().throwErrorIfStrict("Menu items cannot handle children tags other than sub-menus. Found child: "
                + childTag.getTagName() + " with actor: " + childTag.getActor());
        // Non-strict parser will append any child:
        super.handleValidChild(childTag);
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        final MenuItem item = getMenuItem();
        final String textToAppend = getParser().parseString(plainTextLine, getActor());
        if (Strings.isEmpty(item.getText())) {
            item.setText(textToAppend);
        } else {
            item.setText(item.getText().toString() + textToAppend);
        }
    }

    /** @return casted actor. */
    protected MenuItem getMenuItem() {
        return (MenuItem) getActor();
    }

    @Override
    protected boolean hasComponentActors() {
        return true;
    }

    @Override
    protected Actor[] getComponentActors(final Actor actor) {
        final MenuItem item = (MenuItem) actor;
        if (item.getImage() != null) {
            return new Actor[] { item.getLabel(), item.getImage() };
        }
        // Label is never null:
        return new Actor[] { item.getLabel() };
    }
}
