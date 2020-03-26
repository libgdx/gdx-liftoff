package com.github.czyzby.lml.parser.impl.attribute;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.github.czyzby.kiwi.util.common.Exceptions;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Allows to choose touchable status. Expects a string matching Touchable enum name. By default, mapped to "touchable"
 * attribute name.
 *
 * @author MJ */
public class TouchableLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        actor.setTouchable(getTouchable(parser.parseString(rawAttributeData, actor), parser));
    }

    private static Touchable getTouchable(final String touchableName, final LmlParser parser) {
        try {
            final Touchable touchable = Touchable.valueOf(touchableName);
            if (touchable != null) {
                return touchable;
            }
        } catch (final Exception exception) {
            Exceptions.ignore(exception); // Somewhat expected. Might still match ignoring case.
        }
        for (final Touchable touchable : Touchable.values()) {
            if (touchable.name().equalsIgnoreCase(touchableName)) {
                return touchable;
            }
        }
        parser.throwErrorIfStrict("Invalid touchable enum name: " + touchableName);
        return Touchable.enabled;
    }
}
