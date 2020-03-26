package com.github.czyzby.lml.vis.parser.impl.attribute.picker;

import com.badlogic.gdx.graphics.Color;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;

/** See {@link ColorPicker#setListener(ColorPickerListener)}. Requires an action ID that references a method consuming
 * {@link Color} instance. Will construct a listener which invokes the method when a color is chosen or color picker is
 * cancelled (will invoke method with old color value). Will cancel {@link ColorPickerResponsiveListenerLmlAttribute}
 * setting. If you need to attach a more complex listener, you can provide your own implementation in Java after getting
 * reference of the picker with {@link com.github.czyzby.lml.parser.impl.attribute.OnCreateLmlAttribute} or
 * {@link com.github.czyzby.lml.parser.impl.attribute.OnCloseLmlAttribute}. Mapped to "listener".
 *
 * @author MJ */
public class ColorPickerListenerLmlAttribute implements LmlAttribute<ColorPicker> {
    @Override
    public Class<ColorPicker> getHandledType() {
        return ColorPicker.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final ColorPicker actor,
            final String rawAttributeData) {
        final ActorConsumer<?, Color> listener = parser.parseAction(rawAttributeData, Color.WHITE);
        if (listener == null) {
            parser.throwErrorIfStrict(
                    "Color picker listener attribute needs a reference to an action that consumes a Color instance. No method found for ID: "
                            + rawAttributeData);
            return;
        }
        actor.setListener(getListener(listener));
    }

    /** @param listener consumes a color. Should be converted to a listener.
     * @return a new instance of {@link ColorPickerListener} constructed with passed listener method. */
    protected ColorPickerListener getListener(final ActorConsumer<?, Color> listener) {
        return prepareColorPickerListener(listener);
    }

    /** @param listener a non-null method that consumes colors.
     * @return an instance of {@link ColorPickerListener} that should be attached to currently shown {@link ColorPicker}
     *         instance. */
    public static ColorPickerListener prepareColorPickerListener(final ActorConsumer<?, Color> listener) {
        return new ColorPickerAdapter() {
            @Override
            public void canceled(final Color oldColor) {
                finished(oldColor);
            }

            @Override
            public void finished(final Color newColor) {
                listener.consume(newColor);
            }
        };
    }
}
