package com.github.czyzby.lml.parser.impl.attribute.progress;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link ProgressBar#setAnimateDuration(float)}. Mapped to "animateDuration", "animate", "animation".
 *
 * @author MJ */
public class AnimateDurationLmlAttribute implements LmlAttribute<ProgressBar> {
    @Override
    public Class<ProgressBar> getHandledType() {
        return ProgressBar.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final ProgressBar actor,
            final String rawAttributeData) {
        actor.setAnimateDuration(parser.parseFloat(rawAttributeData, actor));
    }
}
