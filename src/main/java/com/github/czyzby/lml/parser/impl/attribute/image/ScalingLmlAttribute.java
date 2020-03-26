package com.github.czyzby.lml.parser.impl.attribute.image;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.github.czyzby.kiwi.util.common.Exceptions;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Image#setScaling(Scaling)}. Expects a string that matches {@link Scaling} enum constant (ideally - it
 * should be the same as enum constant name, but can be equal ignoring case). Mapped to "scaling", "imageScaling".
 *
 * @author MJ */
public class ScalingLmlAttribute implements LmlAttribute<Image> {
    @Override
    public Class<Image> getHandledType() {
        return Image.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Image actor, final String rawAttributeData) {
        actor.setScaling(parseScaling(parser, parser.parseString(rawAttributeData, actor)));
    }

    private static Scaling parseScaling(final LmlParser parser, final String parsedData) {
        try {
            final Scaling scaling = Scaling.valueOf(parsedData);
            if (scaling != null) {
                return scaling;
            }
        } catch (final Exception exception) {
            Exceptions.ignore(exception); // Somewhat expected. Invalid name.
        }
        for (final Scaling scaling : Scaling.values()) {
            if (parsedData.equalsIgnoreCase(scaling.name())) {
                return scaling;
            }
        }
        parser.throwErrorIfStrict("Unable to find Scaling enum constant with name: " + parsedData);
        return Scaling.stretch;
    }
}
