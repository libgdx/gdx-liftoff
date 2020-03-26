package com.github.czyzby.lml.parser.impl.attribute;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Finds and stores referenced action to be invoked after the actor is fully initiated. As opposed to
 * {@link OnCloseLmlAttribute}, these actions are invoked after actor object is fully constructed, rather than the tag
 * is closed, so the widget will not contain its children and will not have yet parsed its plain text between tags.
 * Multiple on create actions can be referenced with different attribute names, but their invocation order is not fixed,
 * so they should not depend on each other. This attribute expects an action ID. By default, mapped to "onCreate",
 * "create", "onInit", "init".
 *
 * @author MJ */
public class OnCreateLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        final ActorConsumer<?, Object> action = parser.parseAction(rawAttributeData, (Object) actor);
        if (action != null) {
            LmlUtilities.getLmlUserObject(actor).addOnCreateAction(action);
        } else {
            parser.throwErrorIfStrict(
                    "Unable to find on create action for actor: " + actor + " with action ID: " + rawAttributeData);
        }
    }
}
