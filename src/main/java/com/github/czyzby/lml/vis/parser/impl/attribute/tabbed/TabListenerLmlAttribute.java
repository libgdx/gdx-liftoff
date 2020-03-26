package com.github.czyzby.lml.vis.parser.impl.attribute.tabbed;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;

/** See {@link TabbedPane#addListener(TabbedPaneListener)}. Expects an action ID that consumes a {@link TabbedPane} (or
 * nothing) and returns an implementation of {@link TabbedPaneListener}. Mapped to "tabListener", "tabbedPaneListener".
 *
 * @author MJ */
public class TabListenerLmlAttribute extends AbstractTabbedPaneLmlAttribute {
    @Override
    protected void process(final LmlParser parser, final LmlTag tag, final TabbedPane tabbedPane,
            final String rawAttributeData) {
        final ActorConsumer<?, TabbedPane> action = parser.parseAction(rawAttributeData, tabbedPane);
        if (action == null) {
            parser.throwErrorIfStrict(
                    "Tab listener attribute requires an action ID. Action not found for ID: " + rawAttributeData);
            return;
        }
        final Object result = action.consume(tabbedPane);
        if (result instanceof TabbedPaneListener) {
            tabbedPane.addListener((TabbedPaneListener) result);
        } else {
            parser.throwErrorIfStrict(
                    "Action referenced in tab listener attribute has to return an instance of TabbedPaneListener. Found action with ID: "
                            + rawAttributeData + " that returned: " + result);
        }
    }
}
