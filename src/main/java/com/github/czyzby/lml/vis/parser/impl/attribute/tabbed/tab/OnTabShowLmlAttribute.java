package com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.tab;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.ui.VisTabTable;

/** See {@code VisTabTable#setOnShow(ActorConsumer)}. Expects an action ID; the action will be invoked each time the tab
 * is shown. Only one action is permitted; using this attribute multiple times will override previous "listeners".
 * Mapped to "onTabShow".
 *
 * @author MJ */
public class OnTabShowLmlAttribute implements LmlAttribute<VisTabTable> {
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
                    "Tab showing method expects an action ID of a method that consumes a VisTabTable. Method not found for ID: "
                            + rawAttributeData);
            return;
        }
        actor.setOnShow(action);
    }
}
