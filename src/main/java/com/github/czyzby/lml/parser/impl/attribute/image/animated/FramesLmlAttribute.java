package com.github.czyzby.lml.parser.impl.attribute.image.animated;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.AnimatedImage;

/** This attribute complements the default
 * {@link com.github.czyzby.lml.parser.impl.attribute.building.StyleLmlAttribute}. However, this is not just an alias:
 * style attribute is parsed BEFORE the widget is created, so if you pass an action reference in style attribute, the
 * invoked action will receive no argument. Frames attribute is parsed DURING actor creation, after its instance already
 * exists, so it can properly handle method references.
 *
 * <p>
 * This attribute expects an LML array of drawables stored in the default skin. Note that this attribute will NOT
 * override frames passed in the style attribute: it will append new frames to the existing frames array, making it
 * possible to use frames from two separate skins (if needed). Mapped to "frames".
 *
 * @author MJ */
public class FramesLmlAttribute implements LmlAttribute<AnimatedImage> {
    @Override
    public Class<AnimatedImage> getHandledType() {
        return AnimatedImage.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final AnimatedImage actor,
            final String rawAttributeData) {
        final Skin skin = parser.getData().getDefaultSkin();
        final Array<Drawable> frames = actor.getFrames();
        for (final String frame : parser.parseArray(rawAttributeData, actor)) {
            frames.add(skin.getDrawable(frame));
        }
        actor.validateCurrentFrame();
    }
}
