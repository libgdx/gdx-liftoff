package com.github.czyzby.lml.vis.parser.impl.attribute.tabbed;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;

/** See {@link TabbedPane#switchTab(int)}. Set after all tabbed pane's children are parsed. Mapped to "selected",
 * "selectedTab".
 *
 * @author MJ */
public class TabSelectedLmlAttribute extends AbstractTabbedPaneLmlAttribute {
    @Override
    protected void process(final LmlParser parser, final LmlTag tag, final TabbedPane tabbedPane,
            final String rawAttributeData) {
        LmlUtilities.getLmlUserObject(tabbedPane.getTable()).addOnCloseAction(new ActorConsumer<Object, Object>() {
            @Override
            public Object consume(final Object actor) {
                tabbedPane.switchTab(parser.parseInt(rawAttributeData, tabbedPane));
                return null;
            }
        });
    }
}
