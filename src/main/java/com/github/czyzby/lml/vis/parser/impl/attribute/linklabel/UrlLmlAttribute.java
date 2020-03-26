package com.github.czyzby.lml.vis.parser.impl.attribute.linklabel;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.LinkLabel;

/** See {@link LinkLabel#setUrl(CharSequence)}. Mapped to "url", "href".
 * 
 * @author Kotcrab */
public class UrlLmlAttribute implements LmlAttribute<LinkLabel> {
    @Override
    public Class<LinkLabel> getHandledType() {
        return LinkLabel.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final LinkLabel actor,
            final String rawAttributeData) {
        actor.setUrl(parser.parseString(rawAttributeData, actor));
    }
}
