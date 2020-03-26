package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.actor.TableLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;

/** Handles {@link MenuBar} widgets. Parses all {@link TableLmlTag} attributes, setting them in
 * {@link MenuBar#getTable()} widget. While technically it can handle children with cell attributes, they should NOT be
 * used for proper behavior; standard menu bars can handle only {@link MenuLmlTag} children. (When parser is not strict,
 * menu bars can handle any children, including labels created from plain text between tags.) Still, bar's look can be
 * fully customized with cell defaults attributes. Mapped to "menuBar", "bar".
 *
 * @author MJ */
public class MenuBarLmlTag extends TableLmlTag {
    private MenuBar menuBar;

    public MenuBarLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        menuBar = new MenuBar(builder.getStyleName());
        return menuBar.getTable();
    }

    /** @return managed {@link MenuBar} object. */
    @Override
    public Object getManagedObject() {
        return menuBar;
    }

    @Override
    protected void addChild(final Actor actor) {
        if (actor instanceof Menu) {
            menuBar.addMenu((Menu) actor);
        } else {
            getParser().throwErrorIfStrict("Menu bars can handle only menu children. Found child: " + actor);
            super.addChild(actor);
        }
    }
}
