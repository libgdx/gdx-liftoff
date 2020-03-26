package com.github.czyzby.lml.vis.parser.impl.attribute.menu;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.PopupMenu;

/**
 * See {@link PopupMenu#setListener(PopupMenu.PopupMenuListener)}. Expects ID of an action that returns
 * {@link PopupMenu.PopupMenuListener} instance. Mapped to "menuListener".
 * @author MJ
 */
public class PopupMenuListenerLmlAttribute implements LmlAttribute<PopupMenu> {
    @Override
    public Class<PopupMenu> getHandledType() {
        return PopupMenu.class;
    }

    @Override
    public void process(LmlParser parser, LmlTag tag, PopupMenu popupMenu, String rawAttributeData) {
        @SuppressWarnings("unchecked")
        final ActorConsumer<PopupMenu.PopupMenuListener, PopupMenu> action = (ActorConsumer<PopupMenu.PopupMenuListener, PopupMenu>) parser
                .parseAction(rawAttributeData, popupMenu);
        if (action == null) {
            parser.throwErrorIfStrict(
                    "Popup menu listener attribute expects an action that returns PopupMenuListener. Method not found for ID: "
                            + rawAttributeData);
            return;
        }
        popupMenu.setListener(action.consume(popupMenu));
    }
}
