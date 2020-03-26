package com.github.czyzby.lml.parser.impl.attribute;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Allows to set actor's color. Expects A) a string, matching a color's name in the default Skin or B) an action ID
 * that returns a Color instance. If an action ID is passed (string proceeded with method invocation marker, by default
 * - '$'), but does not return a Color instance, its result will be converted to string and used as color name to look
 * for in the skin. By default, mapped to "color" attribute name.
 *
 * @author MJ */
public class ColorLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        String colorName;
        if (Strings.startsWith(rawAttributeData, parser.getSyntax().getMethodInvocationMarker())) {
            final ActorConsumer<?, Actor> action = parser.parseAction(rawAttributeData, actor);
            if (action == null) {
                parser.throwError("Invalid action ID: " + rawAttributeData + " for actor: " + actor);
            }
            final Object actionResult = action.consume(actor);
            if (actionResult instanceof Color) {
                actor.setColor((Color) actionResult);
                return;
            }
            colorName = Strings.toString(actionResult);
        } else {
            colorName = parser.parseString(rawAttributeData, actor);
        }
        try {
            actor.setColor(parser.getData().getDefaultSkin().getColor(colorName));
        } catch (final Exception exception) {
            parser.throwErrorIfStrict("Unable to obtain color with name: " + colorName, exception);
        }
    }
}
