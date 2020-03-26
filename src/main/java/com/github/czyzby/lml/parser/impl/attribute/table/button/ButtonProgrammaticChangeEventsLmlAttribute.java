package com.github.czyzby.lml.parser.impl.attribute.table.button;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Button#setProgrammaticChangeEvents(boolean)}. Mapped to "programmaticChangeEvents".
 *
 * @author MJ */
public class ButtonProgrammaticChangeEventsLmlAttribute implements LmlAttribute<Button> {
    @Override
    public Class<Button> getHandledType() {
        return Button.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Button actor, final String rawAttributeData) {
        actor.setProgrammaticChangeEvents(parser.parseBoolean(rawAttributeData, actor));
    }
}
