package com.github.czyzby.lml.parser.impl.attribute.building;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUserObject.StandardTableTarget;

/** Applies to children of {@link com.badlogic.gdx.scenes.scene2d.ui.Dialog}. If a Dialog child has a on result action
 * attached, it will be automatically appended to the buttons table thanks to this parser. See
 * {@code com.badlogic.gdx.scenes.scene2d.ui.Dialog#getButtonTable()}. By default, mapped to the same attributes as on
 * result action attribute processor.
 *
 * @author MJ */
public class OnResultInitialLmlAttribute implements LmlBuildingAttribute<LmlActorBuilder> {
    @Override
    public Class<LmlActorBuilder> getBuilderType() {
        return LmlActorBuilder.class;
    }

    @Override
    public boolean process(final LmlParser parser, final LmlTag tag, final LmlActorBuilder builder,
            final String rawAttributeData) {
        builder.setTableTarget(StandardTableTarget.BUTTON);
        // We don't want to prevent the on result parser from handling this attribute:
        return NOT_FULLY_PARSED;
    }
}