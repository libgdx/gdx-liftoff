package com.github.czyzby.lml.parser.impl.attribute.select;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.github.czyzby.kiwi.util.common.Exceptions;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Allows to set initial selected item in the SelectBox, eventually calling {@link SelectBox#setSelectedIndex(int)} or
 * {@link SelectBox#setSelected(Object)} method when the SelectBox is fully created. Expects an int (selection index) or
 * String (value to select). Mapped to "selected", "select", "value".
 *
 * @author MJ */
public class SelectBoxSelectedLmlAttribute implements LmlAttribute<SelectBox<?>> {
    @Override
    @SuppressWarnings("unchecked")
    public Class<SelectBox<?>> getHandledType() {
        // Double cast as there were a problem with generics - SomeClass.class cannot be returned as
        // <Class<SomeClass<?>>, even though casting never throws ClassCastException in the end.
        return (Class<SelectBox<?>>) (Object) SelectBox.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final SelectBox<?> actor,
            final String rawAttributeData) {
        LmlUtilities.getLmlUserObject(actor).addOnCloseAction(new ActorConsumer<Object, Object>() {
            @Override
            @SuppressWarnings("unchecked")
            public Object consume(final Object widget) {
                try {
                    actor.setSelectedIndex(parser.parseInt(rawAttributeData, actor));
                } catch (final Exception exception) {
                    Exceptions.ignore(exception); // Possible number format exception. Trying to select string.
                    ((SelectBox<String>) actor).setSelected(parser.parseString(rawAttributeData, actor));
                }
                return null;
            }
        });
    }
}
