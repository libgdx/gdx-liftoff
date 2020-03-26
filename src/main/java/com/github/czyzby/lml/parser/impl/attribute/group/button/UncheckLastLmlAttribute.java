package com.github.czyzby.lml.parser.impl.attribute.group.button;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.ButtonTable;

/** See {@link com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup#setUncheckLast(boolean)}. Mapped to "uncheckLast".
 *
 * @author MJ */
public class UncheckLastLmlAttribute implements LmlAttribute<ButtonTable> {
    @Override
    public Class<ButtonTable> getHandledType() {
        return ButtonTable.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final ButtonTable actor,
            final String rawAttributeData) {
        actor.getButtonGroup().setUncheckLast(parser.parseBoolean(rawAttributeData, actor));
    }
}
