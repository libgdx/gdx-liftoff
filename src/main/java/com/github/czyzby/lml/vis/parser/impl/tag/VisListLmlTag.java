package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.actor.ListLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisList;

/** Handles {@link VisList} actors. Converts label and text button children to items by extracting their text; does not
 * accept any other child tags. Appends plain text lines between tags as its list items. Mapped to "list".
 *
 * @author Kotcrab */
public class VisListLmlTag extends ListLmlTag {
    public VisListLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new VisList<String>(builder.getStyleName());
    }
}
