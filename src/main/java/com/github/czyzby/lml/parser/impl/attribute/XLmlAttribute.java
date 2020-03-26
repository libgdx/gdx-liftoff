package com.github.czyzby.lml.parser.impl.attribute;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Allows to specify initial position of the actor on the stage. Expends a float value (absolute position).
 *
 * <p>
 * Note that it works MOSTLY if the actor is a root - has no parent and is attached to a stage by the parser, not
 * manually. Most groups will override this setting. If used on a root actor, it can accept float values (absolute
 * position) or a float ending with a '%' (for example, "0.5%"). If a percent is passed, it will set the X position as a
 * percent of stage's initial height. For the most predictable results, set both x and y attributes.
 *
 * <p>
 * By default, mapped to "x".
 *
 * @author MJ
 * @see YLmlAttribute */
public class XLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        if (tag.getParent() == null) { // Attaching to stage with selected position:
            LmlUtilities.getLmlUserObject(actor).parseXPosition(parser, actor, rawAttributeData);
        } else {
            actor.setX(parser.parseFloat(rawAttributeData, actor));
        }
    }
}
