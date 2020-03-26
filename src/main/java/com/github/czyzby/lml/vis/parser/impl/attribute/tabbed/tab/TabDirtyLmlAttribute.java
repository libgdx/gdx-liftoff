package com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.tab;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.github.czyzby.lml.vis.ui.VisTabTable;

/** See {@link com.kotcrab.vis.ui.widget.tabbedpane.Tab#setDirty(boolean)}. Mapped to "dirty".
 *
 * @author MJ */
public class TabDirtyLmlAttribute implements LmlAttribute<VisTabTable> {
    @Override
    public Class<VisTabTable> getHandledType() {
        return VisTabTable.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final VisTabTable actor,
            final String rawAttributeData) {
        // Dirty setting has to be managed before other settings (savable in particular), so we're adding this as an
        // onCreate action:
        LmlUtilities.getLmlUserObject(actor).addOnCreateAction(new ActorConsumer<Object, Object>() {
            @Override
            public Object consume(final Object widget) {
                actor.getTab().setDirty(parser.parseBoolean(rawAttributeData, actor));
                return null;
            }
        });
    }
}
