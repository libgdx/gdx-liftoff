package com.github.czyzby.lml.parser.impl.attribute;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Attaches a ChangleListener to the the actor, invoking a chosen action upon change event. Expects an action ID. By
 * default, mapped to "onChange" and "change" attribute names.
 *
 * @author MJ */
public class OnChangeLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        final ActorConsumer<?, Actor> action = parser.parseAction(rawAttributeData, actor);
        if (action == null) {
            parser.throwError("Could not find action for: " + rawAttributeData + " with actor: " + actor);
        }
        actor.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor widget) {
                action.consume(actor);
            }
        });
    }
}
