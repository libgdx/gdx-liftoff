package com.github.czyzby.lml.parser.impl.attribute.container;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** See {@link Container#setBackground(com.badlogic.gdx.scenes.scene2d.utils.Drawable)}. Expects a name of a drawable in
 * the default skin. Mapped to "background", "bg".
 *
 * @author MJ */
public class ContainerBackgroundLmlAttribute implements LmlAttribute<Container<?>> {
    @Override
    @SuppressWarnings("unchecked")
    public Class<Container<?>> getHandledType() {
        // Double cast as there were a problem with generics - SomeClass.class cannot be returned as
        // <Class<SomeClass<?>>, even though casting never throws ClassCastException in the end.
        return (Class<Container<?>>) (Object) Container.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Container<?> actor,
            final String rawAttributeData) {
        actor.setBackground(parser.getData().getDefaultSkin().getDrawable(parser.parseString(rawAttributeData, actor)));
    }
}
