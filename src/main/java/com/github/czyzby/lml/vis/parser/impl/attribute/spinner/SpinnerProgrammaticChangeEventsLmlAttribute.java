package com.github.czyzby.lml.vis.parser.impl.attribute.spinner;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

/** See {@link Spinner#setProgrammaticChangeEvents(boolean)}. Mapped to "programmaticChangeEvents".
 *
 * @author MJ */
public class SpinnerProgrammaticChangeEventsLmlAttribute implements LmlAttribute<Spinner> {
    @Override
    public Class<Spinner> getHandledType() {
        return Spinner.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Spinner actor, final String rawAttributeData) {
        actor.setProgrammaticChangeEvents(parser.parseBoolean(rawAttributeData, actor));
    }
}
