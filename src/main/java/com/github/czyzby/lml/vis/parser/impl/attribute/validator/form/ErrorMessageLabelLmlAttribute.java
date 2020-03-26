package com.github.czyzby.lml.vis.parser.impl.attribute.validator.form;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.ui.VisFormTable;

/** Expects a boolean. If returns true, this label will be set as current error message label in form with
 * {@link VisFormTable#setMessageLabel(Label)}. Mapped to "errorMessage", "errorLabel", "errorMsgLabel,
 * "errorMessageLabel".
 *
 * @author MJ */
public class ErrorMessageLabelLmlAttribute extends AbstractFormChildLmlAttribute<Label> {
    @Override
    public Class<Label> getHandledType() {
        return Label.class;
    }

    @Override
    protected void processFormAttribute(final LmlParser parser, final LmlTag tag, final VisFormTable parent,
            final Label actor, final String rawAttributeData) {
        if (parser.parseBoolean(rawAttributeData, actor)) {
            parent.setMessageLabel(actor);
        }
    }
}
