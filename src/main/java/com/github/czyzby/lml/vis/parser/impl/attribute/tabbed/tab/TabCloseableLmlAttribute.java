package com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.tab;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.ui.VisTabTable;

/** See {@link VisTabTable.TableTab#setCloseableByUser(boolean)}. Mapped to "closeable", "closeableByUser".
 *
 * @author MJ */
public class TabCloseableLmlAttribute implements LmlAttribute<VisTabTable> {
    @Override
    public Class<VisTabTable> getHandledType() {
        return VisTabTable.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisTabTable actor,
            final String rawAttributeData) {
        actor.getTab().setCloseableByUser(parser.parseBoolean(rawAttributeData, actor));
    }
}
