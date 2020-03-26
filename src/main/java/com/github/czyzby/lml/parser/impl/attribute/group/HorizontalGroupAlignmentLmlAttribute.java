package com.github.czyzby.lml.parser.impl.attribute.group;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** See {@link HorizontalGroup#align(int)}. Expects a string matching (equal or equal ignoring case) enum constant of
 * {@link com.github.czyzby.kiwi.util.gdx.scene2d.Alignment}. Mapped to "groupAlign".
 *
 * @author MJ */
public class HorizontalGroupAlignmentLmlAttribute implements LmlAttribute<HorizontalGroup> {
    @Override
    public Class<HorizontalGroup> getHandledType() {
        return HorizontalGroup.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final HorizontalGroup actor,
            final String rawAttributeData) {
        actor.align(LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
    }
}
