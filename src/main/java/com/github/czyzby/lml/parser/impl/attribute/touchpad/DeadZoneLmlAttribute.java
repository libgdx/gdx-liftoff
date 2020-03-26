package com.github.czyzby.lml.parser.impl.attribute.touchpad;

import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@code Touchpad#setDeadzone(float)}. Mapped to "deadzone", "deadzoneRadius".
 *
 * @author MJ */
public class DeadZoneLmlAttribute implements LmlAttribute<Touchpad> {
    @Override
    public Class<Touchpad> getHandledType() {
        return Touchpad.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Touchpad actor, final String rawAttributeData) {
        actor.setDeadzone(parser.parseFloat(rawAttributeData, actor));
    }
}
