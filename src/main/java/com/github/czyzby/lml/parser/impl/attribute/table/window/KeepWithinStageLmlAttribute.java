package com.github.czyzby.lml.parser.impl.attribute.table.window;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Window#setKeepWithinStage(boolean)}. Mapped to "keepWithin", "keepWithinStage".
 *
 * @author MJ */
public class KeepWithinStageLmlAttribute implements LmlAttribute<Window> {
    @Override
    public Class<Window> getHandledType() {
        return Window.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Window actor, final String rawAttributeData) {
        actor.setKeepWithinStage(parser.parseBoolean(rawAttributeData, actor));
    }
}
