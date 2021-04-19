package com.github.czyzby.lml.parser.impl.attribute.image;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ObjectMap;
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

    private static final ObjectMap<String, Scaling> scalingMap = new ObjectMap<>();
    static {
        scalingMap.put("fit", Scaling.fit);
        scalingMap.put("fill", Scaling.fill);
        scalingMap.put("fillX", Scaling.fillX);
        scalingMap.put("fillY", Scaling.fillY);
        scalingMap.put("stretch", Scaling.stretch);
        scalingMap.put("stretchX", Scaling.stretchX);
        scalingMap.put("stretchY", Scaling.stretchY);
        scalingMap.put("none", Scaling.none);
    }
    private static Scaling parseScaling(final LmlParser parser, final String parsedData) {
        try {
            final Scaling scaling = scalingMap.get(parsedData, Scaling.none);
            if (scaling != null) {
                return scaling;
            }
        } catch (final Exception exception) {
            Exceptions.ignore(exception); // Somewhat expected. Invalid name.
        }
        for (final ObjectMap.Entry<String, Scaling> entry : scalingMap.entries()) {
            if (parsedData.equalsIgnoreCase(entry.key)) {
                return entry.value;
            }
        }
        parser.throwErrorIfStrict("Unable to find Scaling enum constant with name: " + parsedData);
        return Scaling.stretch;
    }
}
