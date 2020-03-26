package com.github.czyzby.lml.parser.impl.attribute.table.button;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Button#setChecked(boolean)}. Mapped to "checked".
 *
 * @author MJ */
public class CheckedLmlAttribute implements LmlAttribute<Button> {
    @Override
    public Class<Button> getHandledType() {
        return Button.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Button actor, final String rawAttributeData) {
        // We don't want to invoke change listeners just yet.
        actor.setProgrammaticChangeEvents(false); // True by default.
        actor.setChecked(parser.parseBoolean(rawAttributeData, actor));
        actor.setProgrammaticChangeEvents(true); // Switching to default state. No getter, so we are basically guessing.
    }
}
