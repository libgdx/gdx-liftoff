package com.github.czyzby.lml.vis.parser.impl.attribute;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.util.BorderOwner;

/** See {@link BorderOwner#setFocusBorderEnabled(boolean)}. Mapped to "focusBorder", "focusBorderEnabled".
 *
 * @author MJ */
public class FocusBorderEnabledLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        if (actor instanceof BorderOwner) {
            ((BorderOwner) actor).setFocusBorderEnabled(parser.parseBoolean(rawAttributeData, actor));
        } else {
            parser.throwErrorIfStrict(
                    "Focus border enabled attribute is available only for actors that implement BorderOwner. Found this attribute in tag of actor: "
                            + actor);
        }
    }
}
