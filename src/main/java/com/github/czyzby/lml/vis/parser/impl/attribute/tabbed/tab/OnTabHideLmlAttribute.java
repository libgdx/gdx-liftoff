package com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.tab;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.ui.VisTabTable;

/** See {@code VisTabTable#setOnHide(ActorConsumer)}. Expects an action ID; the action will be invoked each time the tab
 * is hidden. Only one action is permitted; using this attribute multiple times will override previous "listeners".
 * Mapped to "onTabHide".
 *
 * @author MJ */
public class OnTabHideLmlAttribute implements LmlAttribute<VisTabTable> {
    @Override
    public Class<VisTabTable> getHandledType() {
        return VisTabTable.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisTabTable actor,
            final String rawAttributeData) {
        final ActorConsumer<?, VisTabTable> action = parser.parseAction(rawAttributeData, actor);
        if (action == null) {
            parser.throwErrorIfStrict(
                    "Tab hiding method expects an action ID of a method that consumes a VisTabTable. Method not found for ID: "
                            + rawAttributeData);
            return;
        }
        actor.setOnHide(action);
    }
}
