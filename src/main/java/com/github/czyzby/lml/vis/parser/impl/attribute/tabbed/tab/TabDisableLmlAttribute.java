package com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.tab;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.ui.VisTabTable;

/** See {@link VisTabTable#setDisabled(boolean)}. Mapped to "disable", "disabled".
 *
 * @author MJ */
public class TabDisableLmlAttribute implements LmlAttribute<VisTabTable> {
    @Override
    public Class<VisTabTable> getHandledType() {
        return VisTabTable.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisTabTable actor,
            final String rawAttributeData) {
        actor.setDisabled(parser.parseBoolean(rawAttributeData, actor));
    }
}
