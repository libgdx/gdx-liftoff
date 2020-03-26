package com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.tab;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.ui.VisTabTable;

/** See {@code VisTabTable#setOnSave(ActorConsumer)}. Expects an action ID; the action will be invoked each time the tab
 * is saved. Only one action is permitted; using this attribute multiple times will override previous "listeners".
 * Action HAS to return a boolean: if it is true, saving was a success. If false, tab will not be set as saved. Mapped
 * to "onTabSave", "onSave".
 *
 * @author MJ */
public class OnTabSaveLmlAttribute implements LmlAttribute<VisTabTable> {
    @Override
    public Class<VisTabTable> getHandledType() {
        return VisTabTable.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process(final LmlParser parser, final LmlTag tag, final VisTabTable actor,
            final String rawAttributeData) {
        final ActorConsumer<?, VisTabTable> action = parser.parseAction(rawAttributeData, actor);
        if (action == null) {
            parser.throwErrorIfStrict(
                    "Tab hiding method expects an action ID of a method that consumes a VisTabTable. Method not found for ID: "
                            + rawAttributeData);
            return;
        }
        actor.setOnSave((ActorConsumer<Boolean, VisTabTable>) action);
    }
}
