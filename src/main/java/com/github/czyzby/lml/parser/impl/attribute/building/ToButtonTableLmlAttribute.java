package com.github.czyzby.lml.parser.impl.attribute.building;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUserObject.StandardTableTarget;

/** Expends a boolean. If true and actor is a child of a {@link com.badlogic.gdx.scenes.scene2d.ui.Dialog}, it will be
 * appended to the buttons table. Done automatically if tag has a on result action attached - setting this attribute to
 * true with a result action attached is redundant. See
 * {@code com.badlogic.gdx.scenes.scene2d.ui.Dialog#getButtonTable()}. By default, mapped to "toButtonTable" attribute
 * name.
 *
 * @author MJ */
public class ToButtonTableLmlAttribute implements LmlBuildingAttribute<LmlActorBuilder> {
    @Override
    public Class<LmlActorBuilder> getBuilderType() {
        return LmlActorBuilder.class;
    }

    @Override
    public boolean process(final LmlParser parser, final LmlTag tag, final LmlActorBuilder builder,
            final String rawAttributeData) {
        if (parser.parseBoolean(rawAttributeData)) {
            builder.setTableTarget(StandardTableTarget.BUTTON);
        }
        return FULLY_PARSED;
    }
}
