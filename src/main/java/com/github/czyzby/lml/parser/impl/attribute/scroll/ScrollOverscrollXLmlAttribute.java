package com.github.czyzby.lml.parser.impl.attribute.scroll;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link ScrollPane#setOverscroll(boolean, boolean)}. Sets X overscroll to the parsed value and Y to default
 * value: true, because there were no getters or individual setters available. If you want to disable both, use
 * {@link ScrollOverscrollLmlAttribute} instead. Mapped to "overscrollX".
 *
 * @author MJ */
public class ScrollOverscrollXLmlAttribute implements LmlAttribute<ScrollPane> {
    @Override
    public Class<ScrollPane> getHandledType() {
        return ScrollPane.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final ScrollPane actor,
            final String rawAttributeData) {
        actor.setOverscroll(parser.parseBoolean(rawAttributeData, actor), true);
    }
}
