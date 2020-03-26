package com.github.czyzby.lml.parser.impl.attribute.group;

import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** See {@link VerticalGroup#align(int)}. Expects a string matching (equal or equal ignoring case) enum constant of
 * {@link com.github.czyzby.kiwi.util.gdx.scene2d.Alignment}. Mapped to "groupAlign".
 *
 * @author MJ */
public class VerticalGroupAlignmentLmlAttribute implements LmlAttribute<VerticalGroup> {
    @Override
    public Class<VerticalGroup> getHandledType() {
        return VerticalGroup.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VerticalGroup actor,
            final String rawAttributeData) {
        actor.align(LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
    }
}
