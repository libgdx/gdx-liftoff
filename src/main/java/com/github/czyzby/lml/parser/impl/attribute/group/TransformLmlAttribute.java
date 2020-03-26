package com.github.czyzby.lml.parser.impl.attribute.group;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Group#setTransform(boolean)}. Mapped to "transform".
 *
 * @author MJ */
public class TransformLmlAttribute implements LmlAttribute<Group> {
    @Override
    public Class<Group> getHandledType() {
        return Group.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Group actor, final String rawAttributeData) {
        actor.setTransform(parser.parseBoolean(rawAttributeData, actor));
    }
}
