package com.github.czyzby.lml.parser.impl.attribute;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Actor#addAction(Action)}. Expects a method ID. Invokes found method, expecting an {@link Action} result.
 * Adds the action to the actor. By default, mapped to "action", "onShow".
 *
 * @author MJ */
public class ActionLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        final ActorConsumer<?, Actor> actionProvider = parser.parseAction(rawAttributeData, actor);
        if (actionProvider == null) {
            parser.throwErrorIfStrict("Could not find action for: " + rawAttributeData);
        }
        final Object result = actionProvider.consume(actor);
        if (result instanceof Action) {
            actor.addAction((Action) result);
        } else {
            parser.throwErrorIfStrict(
                    "Action attribute has to reference a method that returns an instance of Action. Found method, but got non-Action result: "
                            + result);
        }
    }
}
