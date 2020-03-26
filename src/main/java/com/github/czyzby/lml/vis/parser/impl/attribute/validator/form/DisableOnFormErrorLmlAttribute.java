package com.github.czyzby.lml.vis.parser.impl.attribute.validator.form;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.ui.VisFormTable;

/** Sets the selected widget as button to disable if there are any errors in the form. Expects a boolean. If true, calls
 * {@link VisFormTable#addWidgetToDisable(Disableable)}. Mapped to "disableOnError", "disableOnFormError",
 * "formDisable".
 *
 * @author MJ */
public class DisableOnFormErrorLmlAttribute extends AbstractFormChildLmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    protected void processFormAttribute(final LmlParser parser, final LmlTag tag, final VisFormTable parent,
            final Actor actor, final String rawAttributeData) {
        if (actor instanceof Disableable) {
            if (parser.parseBoolean(rawAttributeData, actor)) {
                parent.addWidgetToDisable((Disableable) actor);
            }
        } else {
            parser.throwErrorIfStrict(
                    "Only Disableable widgets can be attached to the form with this attribute. Found widget that does not implement Disableable attribute: "
                            + actor + " with tag: " + tag.getTagName());
        }
    }
}
