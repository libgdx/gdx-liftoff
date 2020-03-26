package com.github.czyzby.lml.parser.impl.attribute.container;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Some attributes of {@link Container} and {@link Cell} overlap - and its fine, usually we want to set them both. This
 * abstract base allows to do that and handles the cell retrieval - implementation can focus on the actual attribute
 * parsing rather than cell management.
 *
 * @author MJ */
public abstract class AbstractSharedContainerAndCellLmlAttribute implements LmlAttribute<Container<?>> {
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
        applyToContainer(parser, tag, actor, rawAttributeData);
        final Cell<?> cell = LmlUtilities.getCell(actor, tag.getParent());
        if (cell != null) {
            applyToCell(actor, cell);
        }
    }

    /** @param parser handles LML template parsing.
     * @param tag contains raw tag data. Allows to access actor's parent.
     * @param actor handled actor instance, casted for convenience.
     * @param rawAttributeData unparsed LML attribute data that should be handled by this attribute processor. Common
     *            data types (string, int, float, boolean, action) are already handled by LML parser implementation, so
     *            make sure to invoke its methods. */
    protected abstract void applyToContainer(LmlParser parser, LmlTag tag, Container<?> actor, String rawAttributeData);

    /** @param actor has the property already parsed and set.
     * @param cell if not null. Contains the container actor. */
    protected abstract void applyToCell(Container<?> actor, Cell<?> cell);
}
