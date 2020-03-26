package com.github.czyzby.lml.vis.parser.impl.attribute.validator.form;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.ui.VisFormTable;

/** See {@link VisFormTable#addCheckedFormButton(Button, String)}. Expects a string - error message that will be
 * displayed if the button is not checked. Mapped to "requireChecked", "formChecked", "notCheckedError",
 * "uncheckedError".
 *
 * @author MJ */
public class RequireCheckedLmlAttribute extends AbstractFormChildLmlAttribute<Button> {
    @Override
    public Class<Button> getHandledType() {
        return Button.class;
    }

    @Override
    protected void processFormAttribute(final LmlParser parser, final LmlTag tag, final VisFormTable parent,
            final Button actor, final String rawAttributeData) {
        parent.addCheckedFormButton(actor, parser.parseString(rawAttributeData, actor));
    }
}
