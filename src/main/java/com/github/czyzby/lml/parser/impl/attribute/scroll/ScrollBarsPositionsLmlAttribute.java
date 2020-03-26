package com.github.czyzby.lml.parser.impl.attribute.scroll;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link ScrollPane#setScrollBarPositions(boolean, boolean)}. Expects a LML array of 2 booleans: vertical and
 * horizontal settings, in that order. If vertical value is true, bar is on the bottom. If horizontal is true, bar is on
 * the right. Both default to true. For example, "scrollBarPositions=true;false". Mapped to "scrollBarPositions",
 * "barPositions".
 *
 * @author MJ */
public class ScrollBarsPositionsLmlAttribute implements LmlAttribute<ScrollPane> {
    @Override
    public Class<ScrollPane> getHandledType() {
        return ScrollPane.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final ScrollPane actor,
            final String rawAttributeData) {
        final String[] setup = parser.parseArray(rawAttributeData, actor);
        if (setup.length != 2) { // Alpha, delay.
            parser.throwErrorIfStrict(
                    "Scroll bar positions setup needs an array with exactly 2 elements (in that order): bar on bottom, bar on right. See ScrollPane#setScrollBarPositions(boolean, boolean).");
            return;
        }
        final boolean bottom = parser.parseBoolean(rawAttributeData, actor);
        final boolean right = parser.parseBoolean(rawAttributeData, actor);
        actor.setScrollBarPositions(bottom, right);
    }
}
