package com.github.czyzby.lml.parser.impl.attribute.scroll;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link ScrollPane#setVelocityX(float)}, {@link ScrollPane#setVelocityY(float)}. Mapped to "velocity".
 *
 * @author MJ */
public class ScrollVelocityLmlAttribute implements LmlAttribute<ScrollPane> {
    @Override
    public Class<ScrollPane> getHandledType() {
        return ScrollPane.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final ScrollPane actor,
            final String rawAttributeData) {
        final float velocity = parser.parseFloat(rawAttributeData, actor);
        actor.setVelocityX(velocity);
        actor.setVelocityY(velocity);
    }
}
