package com.github.czyzby.lml.parser.impl.attribute;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Sets actor's alpha color value. Expects a float. By default, mapped to "alpha" and "a" attribute names.
 *
 * @author MJ */
public class ColorAlphaLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        final Color currentColor = actor.getColor();
        // getColor() returns actor's Color instance, but just to be safe - setting with Actor's API:
        actor.setColor(currentColor.r, currentColor.g, currentColor.b, parser.parseFloat(rawAttributeData, actor));
    }
}
