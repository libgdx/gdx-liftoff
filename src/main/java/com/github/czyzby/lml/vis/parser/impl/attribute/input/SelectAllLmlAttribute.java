package com.github.czyzby.lml.vis.parser.impl.attribute.input;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.widget.VisTextField;

/** Expects a boolean. If true, will eventually invoke {@link VisTextField#selectAll()} after the widget is fully
 * initiated. Mapped to "selectAll".
 *
 * @author MJ */
public class SelectAllLmlAttribute implements LmlAttribute<VisTextField> {
    @Override
    public Class<VisTextField> getHandledType() {
        return VisTextField.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisTextField actor,
            final String rawAttributeData) {
        if (parser.parseBoolean(rawAttributeData, actor)) {
            LmlUtilities.getLmlUserObject(actor).addOnCloseAction(new ActorConsumer<Object, Object>() {
                @Override
                public Object consume(final Object widget) {
                    actor.selectAll();
                    return null;
                }
            });
        }
    }
}
