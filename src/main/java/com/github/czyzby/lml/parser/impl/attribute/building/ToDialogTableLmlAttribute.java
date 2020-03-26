package com.github.czyzby.lml.parser.impl.attribute.building;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUserObject.StandardTableTarget;

/** Expends a boolean. Normally, dialog children are added to the content table. By setting this attribute to true,
 * dialog child will be forced directly to the dialog table with
 * {@code com.badlogic.gdx.scenes.scene2d.ui.Dialog#add(Actor)}. By default, mapped to "toDialogTable".
 *
 * @author MJ */
public class ToDialogTableLmlAttribute implements LmlBuildingAttribute<LmlActorBuilder> {
    @Override
    public Class<LmlActorBuilder> getBuilderType() {
        return LmlActorBuilder.class;
    }

    @Override
    public boolean process(final LmlParser parser, final LmlTag tag, final LmlActorBuilder builder,
            final String rawAttributeData) {
        if (parser.parseBoolean(rawAttributeData)) {
            builder.setTableTarget(StandardTableTarget.DIRECT);
        }
        return FULLY_PARSED;
    }
}
