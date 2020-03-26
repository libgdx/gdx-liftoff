package com.github.czyzby.lml.vis.parser.impl.attribute.tabbed;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

/** See {@link TabbedPane#addListener(com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener)}. Expects an action ID
 * that consumes a {@link TabbedPane} - it will be invoked each time
 * {@link com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener#removedAllTabs()} is invoked. Mapped to
 * "onAllRemoved", "onAllTabsRemoved", "onClear", "onTabsClear".
 *
 * @author MJ */
public class OnAllTabsRemovalLmlAttribute extends AbstractTabbedPaneLmlAttribute {
    @Override
    protected void process(final LmlParser parser, final LmlTag tag, final TabbedPane tabbedPane,
            final String rawAttributeData) {
        final ActorConsumer<?, TabbedPane> action = parser.parseAction(rawAttributeData, tabbedPane);
        if (action == null) {
            parser.throwErrorIfStrict(
                    "All tabs removal listener attribute requires an action ID. Action not found for ID: "
                            + rawAttributeData);
            return;
        }
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void removedAllTabs() {
                action.consume(tabbedPane);
            }
        });
    }
}
