package com.github.czyzby.lml.parser.impl.attribute.scroll;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link ScrollPane#setupOverscroll(float, float, float)}. Expects a LML array of 3 floats: distance, min speed
 * and max speed, in that order. For example, "setupOverscroll="24;42.4;92". Mapped to "setupOverscroll".
 *
 * @author MJ */
public class ScrollOverscrollSetupLmlAttribute implements LmlAttribute<ScrollPane> {
    @Override
    public Class<ScrollPane> getHandledType() {
        return ScrollPane.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final ScrollPane actor,
            final String rawAttributeData) {
        final String[] setup = parser.parseArray(rawAttributeData, actor);
        if (setup.length != 3) { // Distance, speedMin, speedMax.
            parser.throwErrorIfStrict(
                    "Overscroll setup needs an array with exactly 3 elements (in that order): distance, min speed and max speed. See ScrollPane#setupOverscroll(float, float, float).");
            return;
        }
        final float distance = parser.parseFloat(setup[0], actor);
        final float speedMin = parser.parseFloat(setup[1], actor);
        final float speedMax = parser.parseFloat(setup[2], actor);
        actor.setupOverscroll(distance, speedMin, speedMax);
    }
}
