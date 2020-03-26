package com.github.czyzby.lml.parser.impl.attribute.scroll;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link ScrollPane#setupFadeScrollBars(float, float)}. Expects a LML array of 2 floats: fade alpha seconds and
 * fade delay seconds, in that order. For example, "setupFaceScrollBars=0;1". Mapped to "setupFadeScrollBars".
 *
 * @author MJ */
public class ScrollFadeBarsSetupLmlAttribute implements LmlAttribute<ScrollPane> {
    @Override
    public Class<ScrollPane> getHandledType() {
        return ScrollPane.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final ScrollPane actor,
            final String rawAttributeData) {
        final String[] setup = parser.parseArray(rawAttributeData, actor);
        if (setup.length != 2) { // Alpha, delay.
            parser.throwErrorIfStrict(
                    "Fade scroll bars setup needs an array with exactly 2 elements (in that order): fade alpha seconds and fade delay seconds. See ScrollPane#setupFadeScrollBars(float, float).");
            return;
        }
        final float fadeAlphaSeconds = parser.parseFloat(setup[0], actor);
        final float fadeDelaySeconds = parser.parseFloat(setup[1], actor);
        actor.setupFadeScrollBars(fadeAlphaSeconds, fadeDelaySeconds);
    }
}
