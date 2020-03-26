package com.github.czyzby.lml.parser.impl.attribute;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Creates a {@link TextTooltip} with text parsed from attribute value. Expects a string. Tooltip's style is not
 * manageable and will be choose style mapped to "default" in default LML data's skin. Uses default tooltip manager
 * stored in LML data.
 *
 * <p>
 * If an action is passed instead of a regular string, it is parsed differently. If it returns a {@link Tooltip}
 * instance, it will be attached to the actor. If it returns {@link Actor} instance, a tooltip with the actor will be
 * created and attached to the widget with the tooltip attribute. If it returns a different object, it will be converted
 * to string and used to create a {@link TextTooltip}.
 *
 * <p>
 * By default, mapped to "tooltip" attribute name.
 *
 * @author MJ */
public class TooltipLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        final Tooltip<?> tooltip;
        if (Strings.startsWith(rawAttributeData, parser.getSyntax().getMethodInvocationMarker())) {
            tooltip = parseTooltipFromAction(parser, actor, rawAttributeData);
        } else {
            // Parsing regular string and creating a default text tooltip:
            tooltip = new TextTooltip(parser.parseString(rawAttributeData, actor),
                    parser.getData().getDefaultTooltipManager(), parser.getData().getDefaultSkin());
        }
        if (tooltip != null) {
            actor.addListener(tooltip);
        }
    }

    protected Tooltip<?> parseTooltipFromAction(final LmlParser parser, final Actor actor,
            final String rawAttributeData) {
        final ActorConsumer<?, Actor> action = parser.parseAction(rawAttributeData, actor);
        if (action == null) {
            parser.throwErrorIfStrict(
                    "Cannot create tooltip with unknown action ID: " + rawAttributeData + " for actor: " + action);
            return null;
        }
        final Object result = action.consume(actor);
        if (result instanceof Tooltip<?>) {
            // Found an action and its result is a tooltip. Attaching directly.
            return (Tooltip<?>) result;
        } else if (result instanceof Actor) {
            // Found an action and its result is an actor. Converting to a tooltip:
            return new Tooltip<Actor>((Actor) result, parser.getData().getDefaultTooltipManager());
        }
        // Found an action and its result is a different object. Converting to string and constructing tooltip:
        return new TextTooltip(Nullables.toString(result), parser.getData().getDefaultTooltipManager(),
                parser.getData().getDefaultSkin());
    }
}
