package com.github.czyzby.lml.vis.parser.impl.attribute.menu;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.PopupMenu;

/**
 * See {@link MenuBar#setMenuListener(MenuBar.MenuBarListener)}. Expects ID of an action that returns
 * {@link MenuBar.MenuBarListener} instance. Mapped to "menuListener".
 * @author MJ
 */
public class MenuBarListenerLmlAttribute implements LmlAttribute<MenuBar> {
    @Override
    public Class<MenuBar> getHandledType() {
        return MenuBar.class;
    }

    @Override
    public void process(LmlParser parser, LmlTag tag, MenuBar menuBar, String rawAttributeData) {
        @SuppressWarnings("unchecked")
        final ActorConsumer<MenuBar.MenuBarListener, MenuBar> action = (ActorConsumer<MenuBar.MenuBarListener, MenuBar>) parser
                .parseAction(rawAttributeData, menuBar);
        if (action == null) {
            parser.throwErrorIfStrict(
                    "Menu bar listener attribute expects an action that returns MenuBarListener. Method not found for ID: "
                            + rawAttributeData);
            return;
        }
        menuBar.setMenuListener(action.consume(menuBar));
    }
}
