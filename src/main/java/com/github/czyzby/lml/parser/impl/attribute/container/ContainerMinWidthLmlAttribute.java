package com.github.czyzby.lml.parser.impl.attribute.container;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** See {@link Container#minWidth(Value)}. If container is in a table cell, it also
 * calls {@link Cell#minWidth(Value)}. See
 * {@link LmlUtilities#parseHorizontalValue(LmlParser, LmlTag, com.badlogic.gdx.scenes.scene2d.Actor, String)} and
 * {@link LmlUtilities#parseVerticalValue(LmlParser, LmlTag, com.badlogic.gdx.scenes.scene2d.Actor, String)} for more
 * info on value parsing. Mapped to "minWidth".
 *
 * @author MJ */
public class ContainerMinWidthLmlAttribute implements LmlAttribute<Container<?>> {
    // No getter for minValueWidth, so we cannot use abstract base...

    @Override
    @SuppressWarnings("unchecked")
    public Class<Container<?>> getHandledType() {
        // Double cast as there were a problem with generics - SomeClass.class cannot be returned as
        // <Class<SomeClass<?>>, even though casting never throws ClassCastException in the end.
        return (Class<Container<?>>) (Object) Container.class;
    }

    @Override
    public final void process(final LmlParser parser, final LmlTag tag, final Container<?> actor,
            final String rawAttributeData) {
        final Value value = LmlUtilities.parseHorizontalValue(parser, tag.getParent(), actor, rawAttributeData);
        actor.minWidth(value);
        final Cell<?> cell = LmlUtilities.getCell(actor, tag.getParent());
        if (cell != null) {
            cell.minWidth(value);
        }
    }
}
