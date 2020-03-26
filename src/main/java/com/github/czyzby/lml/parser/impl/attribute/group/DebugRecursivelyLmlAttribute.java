package com.github.czyzby.lml.parser.impl.attribute.group;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** See {@link Group#setDebug(boolean, boolean)}. Second method argument is always true, so this method sets debug for
 * all children; for non-recursive debug, use default debug attribute. Mapped to "debugRecursively".
 *
 * @author MJ */
public class DebugRecursivelyLmlAttribute implements LmlAttribute<Group> {
    @Override
    public Class<Group> getHandledType() {
        return Group.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Group actor, final String rawAttributeData) {
        final boolean debugRecursively = parser.parseBoolean(rawAttributeData, actor);
        LmlUtilities.getLmlUserObject(actor).addOnCloseAction(new ActorConsumer<Void, Object>() {
            @Override
            public Void consume(Object widget) {
                actor.setDebug(debugRecursively, true);
                return null;
            }
        });
    }
}
