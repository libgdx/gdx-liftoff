package com.github.czyzby.lml.parser.impl.attribute.building;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUserObject.StandardTableTarget;

/** Expends a boolean. If true and actor is a child of a Window, it will be appended to the title table. See
 * {@code com.badlogic.gdx.scenes.scene2d.ui.Window#getTitleTable()}. By default, mapped to "toTitleTable" attribute
 * name.
 *
 * @author MJ */
public class ToTitleTableLmlAttribute implements LmlBuildingAttribute<LmlActorBuilder> {
    @Override
    public Class<LmlActorBuilder> getBuilderType() {
        return LmlActorBuilder.class;
    }

    @Override
    public boolean process(final LmlParser parser, final LmlTag tag, final LmlActorBuilder builder,
            final String rawAttributeData) {
        if (parser.parseBoolean(rawAttributeData)) {
            builder.setTableTarget(StandardTableTarget.TITLE);
        }
        return FULLY_PARSED;
    }
}
