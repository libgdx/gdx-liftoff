package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.color.BasicColorPicker;

/** Handles {@link BasicColorPicker} actor. Handles children like a table tag. Mapped to "basicColorPicker",
 * "basicPicker".
 *
 * @author MJ */
public class BasicColorPickerLmlTag extends VisTableLmlTag {
    public BasicColorPickerLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new BasicColorPicker(builder.getStyleName(), null);
    }
}
