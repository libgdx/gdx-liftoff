package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractGroupLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Handles {@link HorizontalGroup} actor. Appends plain text between tags as labels. Mapped to "horizontal",
 * "horizontalGroup".
 *
 * @author MJ */
public class HorizontalGroupLmlTag extends AbstractGroupLmlTag {
    public HorizontalGroupLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Group getNewInstanceOfGroup(final LmlActorBuilder builder) {
        return new HorizontalGroup();
    }
}
