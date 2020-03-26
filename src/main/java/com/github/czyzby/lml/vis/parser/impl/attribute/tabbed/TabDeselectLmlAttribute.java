package com.github.czyzby.lml.vis.parser.impl.attribute.tabbed;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;

/** See {@link TabbedPane#setAllowTabDeselect(boolean)}. Mapped to "allowTabDeselect", "tabDeselect".
 *
 * @author MJ */
public class TabDeselectLmlAttribute extends AbstractTabbedPaneLmlAttribute {
    @Override
    protected void process(final LmlParser parser, final LmlTag tag, final TabbedPane tabbedPane,
            final String rawAttributeData) {
        tabbedPane.setAllowTabDeselect(parser.parseBoolean(rawAttributeData, tabbedPane));
    }
}
