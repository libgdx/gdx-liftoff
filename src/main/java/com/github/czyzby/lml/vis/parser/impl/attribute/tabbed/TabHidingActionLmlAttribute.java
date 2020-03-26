package com.github.czyzby.lml.vis.parser.impl.attribute.tabbed;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.tag.TabbedPaneLmlTag;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;

/** See {@link TabbedPaneLmlTag#setHideActionProvider(ActorConsumer)}. Expects an action ID that references a method
 * consuming {@link Tab} (or nothing) and returning an {@link Action}. Invoked each time a tab is hidden. Mapped to
 * "tabHideAction".
 *
 * @author MJ */
public class TabHidingActionLmlAttribute extends AbstractTabbedPaneLmlAttribute {
    @Override
    protected void process(final LmlParser parser, final LmlTag tag, final TabbedPane tabbedPane,
            final String rawAttributeData) {
        if (tag instanceof TabbedPaneLmlTag) {
            @SuppressWarnings("unchecked") final ActorConsumer<Action, Tab> action = (ActorConsumer<Action, Tab>) parser
                    .parseAction(rawAttributeData, MOCK_UP_TAB);
            if (action == null) {
                parser.throwErrorIfStrict(
                        "Unable to find action consuming Tab and returning Action for ID: " + rawAttributeData);
                return;
            }
            ((TabbedPaneLmlTag) tag).setHideActionProvider(action);
        } else {
            parser.throwErrorIfStrict(
                    "Unexpected tag type for tabbed pane tag. Expected: TabbedPaneLmlTag, got: " + tag.getClass());
        }
    }
}