package com.github.czyzby.lml.parser.impl.attribute.table.window;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Window#setModal(boolean)}. Mapped to "modal".
 *
 * @author MJ */
public class ModalLmlAttribute implements LmlAttribute<Window> {
    @Override
    public Class<Window> getHandledType() {
        return Window.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Window actor, final String rawAttributeData) {
        actor.setModal(parser.parseBoolean(rawAttributeData, actor));
    }
}
