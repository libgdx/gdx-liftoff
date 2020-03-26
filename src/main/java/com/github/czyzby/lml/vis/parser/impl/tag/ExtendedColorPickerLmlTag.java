package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.color.ExtendedColorPicker;

/** Handles {@link ExtendedColorPicker} actors. Works like {@link BasicColorPickerLmlTag}, except it contains more
 * widgets and makes it easier to fully customize the chosen color. Mapped to "extendedColorPicker", "extendedPicker".
 *
 * @author MJ */
public class ExtendedColorPickerLmlTag extends BasicColorPickerLmlTag {
    public ExtendedColorPickerLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new ExtendedColorPicker(builder.getStyleName(), null);
    }
}
