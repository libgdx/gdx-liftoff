package com.github.czyzby.lml.vis.parser.impl.attribute;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.attribute.picker.ColorPickerListenerLmlAttribute;
import com.github.czyzby.lml.vis.util.ColorPickerContainer;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;

/** Attaches a {@link ClickListener} that shows a {@link ColorPicker} dialog upon clicking, unless the actor is
 * disabled. Initial picker's color will match the color of the clicked widget: this is especially useful if listener is
 * attached to a simple image, label or text button actor. Expects an action ID that references a method which consumes
 * a {@link Color}. When the dialog is closed, this method will be invoked with the chosen color instance - or the
 * initial, old color value, if the dialog was cancelled. Only one color picker can be shown at a time, so make sure
 * that multiple dialogs cannot be opened all at once. Mapped to "colorPicker".
 *
 * @author MJ
 * @see ResponsiveColorPickerLmlAttribute */
public class ColorPickerLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        final ActorConsumer<?, Color> listener = parser.parseAction(rawAttributeData, Color.WHITE);
        if (listener == null) {
            parser.throwErrorIfStrict(
                    "Color picker attribute needs a reference to an action that consumes a Color instance. No method found for ID: "
                            + rawAttributeData);
            return;
        }
        final ColorPickerListener colorPickerListener = getListener(listener);
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                if (actor instanceof Disableable && ((Disableable) actor).isDisabled()) {
                    return;
                }
                final ColorPicker colorPicker = ColorPickerContainer.requestInstance();
                colorPicker.setListener(null);
                colorPicker.setColor(actor.getColor());
                colorPicker.setListener(colorPickerListener);
                colorPicker.centerWindow();
                actor.getStage().addActor(colorPicker.fadeIn());
            }
        });
    }

    /** @param listener consumes a color. Should be converted to a listener.
     * @return a new instance of {@link ColorPickerListener} constructed with passed listener method. */
    protected ColorPickerListener getListener(final ActorConsumer<?, Color> listener) {
        return ColorPickerListenerLmlAttribute.prepareColorPickerListener(listener);
    }
}
