package com.github.czyzby.lml.parser.impl.attribute;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Sets actor's red color value. Expects a float. By default, mapped to "red" and "r" attribute names.
 *
 * @author MJ */
public class ColorRedLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        final Color currentColor = actor.getColor();
        // getColor() returns actor's Color instance, but just to be safe - setting with Actor's API:
        actor.setColor(parser.parseFloat(rawAttributeData, actor), currentColor.g, currentColor.b, currentColor.a);
    }
}
