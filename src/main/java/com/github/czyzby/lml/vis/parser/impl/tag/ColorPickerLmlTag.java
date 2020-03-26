package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.github.czyzby.lml.vis.parser.impl.action.VisStageAttacher;
import com.github.czyzby.lml.vis.parser.impl.action.VisStageAttacher.PopupAttacher;
import com.github.czyzby.lml.vis.parser.impl.tag.builder.VisWindowLmlActorBuilder;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.color.ColorPicker;

/** Handles {@link ColorPicker} actors. Works like any other table: can append any extra children, converts plain text
 * to labels. Is attached to its parent with a specialized click listener, which will show the dialog each time the
 * widget is not disabled and clicked. Note that, as opposed to colorPicker attribute, a new instance of ColorPicker is
 * created for each tag and you HAVE to manage the instance yourself. {@link ColorPicker#dispose()} has to be eventually
 * called in your code. This tag should be used if you plan to use only a few color pickers and need them fully
 * customized; usually simple colorPicker attribute is more than enough. Mapped to "colorPicker".
 *
 * @author MJ
 * @see com.github.czyzby.lml.vis.parser.impl.attribute.ColorPickerLmlAttribute
 * @see com.github.czyzby.lml.vis.util.ColorPickerContainer */
public class ColorPickerLmlTag extends VisWindowLmlTag {
    public ColorPickerLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected VisWindow getNewInstanceOfVisWindow(final VisWindowLmlActorBuilder builder) {
        final ColorPicker colorPicker = new ColorPicker(builder.getStyleName(), builder.getText(), null);
        LmlUtilities.getLmlUserObject(colorPicker).setStageAttacher(new VisStageAttacher());
        colorPicker.setSkin(getSkin(builder));
        return colorPicker;
    }

    @Override
    public boolean isAttachable() {
        return true;
    }

    @Override
    public void attachTo(final LmlTag tag) {
        final Actor actor = tag.getActor();
        actor.addListener(new PopupAttacher((ColorPicker) getActor(), actor));
    }

    @Override
    protected boolean hasComponentActors() {
        return true;
    }

    @Override
    protected Actor[] getComponentActors(final Actor actor) {
        return new Actor[] { ((ColorPicker) actor).getPicker() };
    }
}
