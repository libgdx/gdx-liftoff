
package com.github.czyzby.lml.vis.parser.impl.attribute.draggable.pane;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.layout.DragPane;

/** Allows to specify ID of the group managed internally by {@link DragPane}. If you need a reference to the group
 * managed by the pane rather than the pane itself, use this method to assign an ID to the {@link WidgetGroup}. Mapped
 * to "groupId".
 *
 * @author MJ
 * @see com.github.czyzby.lml.parser.impl.attribute.IdLmlAttribute */
public class GroupIdLmlAttribute implements LmlAttribute<DragPane> {
    @Override
    public Class<DragPane> getHandledType() {
        return DragPane.class;
    }

    @Override
    public void process(LmlParser parser, LmlTag tag, DragPane actor, String rawAttributeData) {
        WidgetGroup group = actor.getGroup();
        String id = parser.parseString(rawAttributeData, group);
        LmlUtilities.setActorId(group, id);
        parser.getActorsMappedByIds().put(id, group);
    }
}
