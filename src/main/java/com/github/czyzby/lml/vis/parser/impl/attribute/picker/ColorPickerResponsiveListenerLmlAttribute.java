package com.github.czyzby.lml.vis.parser.impl.attribute.picker;

import com.badlogic.gdx.graphics.Color;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;

/** See {@link ColorPicker#setListener(ColorPickerListener)}. Requires an action ID that references a method consuming
 * {@link Color} instance. Will construct a listener which invokes the method each time the color has changed or color
 * picker is cancelled (will invoke method with old color value). Will cancel {@link ColorPickerListenerLmlAttribute}
 * setting. If you need to attach a more complex listener, you can provide your own implementation in Java after getting
 * reference of the picker with {@link com.github.czyzby.lml.parser.impl.attribute.OnCreateLmlAttribute} or
 * {@link com.github.czyzby.lml.parser.impl.attribute.OnCloseLmlAttribute}. Mapped to "responsiveListener".
 *
 * @author MJ */
public class ColorPickerResponsiveListenerLmlAttribute extends ColorPickerListenerLmlAttribute {
    @Override
    protected ColorPickerListener getListener(final ActorConsumer<?, Color> listener) {
        return prepareColorPickerListener(listener);
    }

    /** @param listener a non-null method that consumes colors.
     * @return an instance of {@link ColorPickerListener} that should be attached to currently shown {@link ColorPicker}
     *         instance. */
    public static ColorPickerListener prepareColorPickerListener(final ActorConsumer<?, Color> listener) {
        return new ColorPickerListener() {
            @Override
            public void finished(final Color newColor) {
                changed(newColor);
            }

            @Override
            public void changed(final Color newColor) {
                listener.consume(newColor);
            }

            @Override
            public void canceled(final Color oldColor) {
                changed(oldColor);
            }

            @Override
            public void reset(final Color previousColor, final Color newColor) {
                changed(newColor);
            }
        };
    }
}