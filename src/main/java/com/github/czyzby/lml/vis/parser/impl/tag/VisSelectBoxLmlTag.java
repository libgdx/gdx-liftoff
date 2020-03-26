package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.actor.SelectBoxLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisSelectBox;

/** Handles {@link VisSelectBox} actor. Like the List widget it wraps, select box can handle only string data. Converts
 * label and text button children to items by extracting their text; does not accept any other child tags. Appends plain
 * text lines between tags as its items. Mapped to "selectBox", "visSelectBox".
 *
 * @author Kotcrab */
public class VisSelectBoxLmlTag extends SelectBoxLmlTag {
    public VisSelectBoxLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new VisSelectBox<String>(builder.getStyleName());
    }
}
