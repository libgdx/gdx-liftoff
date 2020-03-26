package com.github.czyzby.lml.vis.parser.impl.attribute.picker.basic;

import com.badlogic.gdx.graphics.Color;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.attribute.picker.ColorPickerResponsiveListenerLmlAttribute;
import com.kotcrab.vis.ui.widget.color.BasicColorPicker;

/** Attaches a simple {@link com.kotcrab.vis.ui.widget.color.ColorPickerListener} implementation to the picker. Expects
 * ID of an action that references a method consuming a {@link Color}. The method is called each time the color is
 * changed, reset or approved. If you need to attach a more complex listener, you can provide your own implementation in
 * Java after getting reference of the picker with
 * {@link com.github.czyzby.lml.parser.impl.attribute.OnCreateLmlAttribute} or
 * {@link com.github.czyzby.lml.parser.impl.attribute.OnCloseLmlAttribute}. Mapped to "listener".
 *
 * @author MJ */
public class BasicColorPickerListenerLmlAttribute implements LmlAttribute<BasicColorPicker> {
    @Override
    public Class<BasicColorPicker> getHandledType() {
        return BasicColorPicker.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final BasicColorPicker actor,
            final String rawAttributeData) {
        final ActorConsumer<?, Color> listener = parser.parseAction(rawAttributeData, Color.WHITE);
        if (listener == null) {
            parser.throwErrorIfStrict(
                    "Basic color picker listener attribute needs a reference to an action that consumes a Color instance. No method found for ID: "
                            + rawAttributeData);
            return;
        }
        actor.setListener(ColorPickerResponsiveListenerLmlAttribute.prepareColorPickerListener(listener));
    }
}
