package com.github.czyzby.lml.parser.impl.attribute.list;

import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link com.badlogic.gdx.scenes.scene2d.utils.ArraySelection#setRangeSelect(boolean)}. Mapped to "rangeSelect".
 *
 * @author MJ */
public class RangeSelectLmlAttribute implements LmlAttribute<List<?>> {
    @Override
    @SuppressWarnings("unchecked")
    public Class<List<?>> getHandledType() {
        // Double cast as there were a problem with generics - SomeClass.class cannot be returned as
        // <Class<SomeClass<?>>, even though casting never throws ClassCastException in the end.
        return (Class<List<?>>) (Object) List.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final List<?> actor, final String rawAttributeData) {
        actor.getSelection().setRangeSelect(parser.parseBoolean(rawAttributeData, actor));
    }
}
