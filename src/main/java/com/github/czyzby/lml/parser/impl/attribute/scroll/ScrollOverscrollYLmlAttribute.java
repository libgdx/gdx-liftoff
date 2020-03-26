package com.github.czyzby.lml.parser.impl.attribute.scroll;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link ScrollPane#setOverscroll(boolean, boolean)}. Sets Y overscroll to the parsed value and X to default
 * value: true, because there were no getters or individual setters available. If you want to disable both, use
 * {@link ScrollOverscrollLmlAttribute} instead. Mapped to "overscrollY".
 *
 * @author MJ */
public class ScrollOverscrollYLmlAttribute implements LmlAttribute<ScrollPane> {
    @Override
    public Class<ScrollPane> getHandledType() {
        return ScrollPane.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final ScrollPane actor,
            final String rawAttributeData) {
        actor.setOverscroll(true, parser.parseBoolean(rawAttributeData, actor));
    }
}
