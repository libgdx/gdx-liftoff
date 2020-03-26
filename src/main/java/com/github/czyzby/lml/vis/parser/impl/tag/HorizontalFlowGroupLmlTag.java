package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.github.czyzby.kiwi.util.gdx.scene2d.Actors;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractGroupLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;

/** Handles {@link HorizontalFlowGroup} actor. Mapped to "horizontalFlow", "hFlow", "horizontalFlowGroup".
 *
 * @author MJ */
public class HorizontalFlowGroupLmlTag extends AbstractGroupLmlTag {
    public HorizontalFlowGroupLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Group getNewInstanceOfGroup(final LmlActorBuilder builder) {
        return new HorizontalFlowGroup();
    }

    @Override
    protected void doOnTagClose() {
        // Necessary, prevents size "hiccup":
        Actors.pack(getActor());
    }
}
