package com.github.czyzby.lml.parser.impl.attribute;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Finds and stores referenced action to be invoked after the actor's tag is closed and all its children are parsed. As
 * opposed to {@link OnCreateLmlAttribute}, these actions are invoked after tag is closed, rather than the actor object
 * is constructed. Multiple on close actions can be referenced with different attribute names, but their invocation
 * order is not fixed, so they should not depend on each other. This attribute expects an action ID. By default, mapped
 * to "onClose", "close", "onTagClose", "tagClose".
 *
 * @author MJ */
public class OnCloseLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        final ActorConsumer<?, Object> action = parser.parseAction(rawAttributeData, (Object) actor);
        if (action != null) {
            LmlUtilities.getLmlUserObject(actor).addOnCloseAction(action);
        } else {
            parser.throwErrorIfStrict(
                    "Unable to find on tag close action for actor: " + actor + " with action ID: " + rawAttributeData);
        }
    }
}
