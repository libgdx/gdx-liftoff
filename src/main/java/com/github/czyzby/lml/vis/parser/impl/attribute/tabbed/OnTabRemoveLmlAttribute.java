package com.github.czyzby.lml.vis.parser.impl.attribute.tabbed;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

/** See {@link TabbedPane#addListener(com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener)}. Expects an action ID
 * that consumes a {@link Tab} - it will be invoked each time
 * {@link com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener#removedTab(Tab)} is invoked. Mapped to "onRemove",
 * "onTabRemove".
 *
 * @author MJ */
public class OnTabRemoveLmlAttribute extends AbstractTabbedPaneLmlAttribute {
    @Override
    protected void process(final LmlParser parser, final LmlTag tag, final TabbedPane tabbedPane,
            final String rawAttributeData) {
        final ActorConsumer<?, Tab> action = parser.parseAction(rawAttributeData, MOCK_UP_TAB);
        if (action == null) {
            parser.throwErrorIfStrict("Tab remove listener attribute requires an action ID. Action not found for ID: "
                    + rawAttributeData);
            return;
        }
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void removedTab(final Tab tab) {
                action.consume(tab);
            }
        });
    }
}
