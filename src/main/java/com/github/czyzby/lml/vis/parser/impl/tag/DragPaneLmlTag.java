package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractGroupLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.tag.builder.DragPaneLmlActorBuilder;
import com.kotcrab.vis.ui.layout.DragPane;
import com.kotcrab.vis.ui.widget.Draggable;

/** Handles {@link DragPane} widgets. Can handle any children, converts plain text to labels. To specify a custom
 * {@link Draggable} listener, use {@link DraggableLmlTag}. Can contain any attributes that its internal group contains
 * - so, for example, if you choose a "grid" group type, drag pane tag will be able to handle all
 * {@link GridGroupLmlTag} attributes. Mapped to "dragPane".
 *
 * @author MJ
 * @see DragPaneLmlActorBuilder.GroupType */
public class DragPaneLmlTag extends AbstractGroupLmlTag {
    public DragPaneLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected LmlActorBuilder getNewInstanceOfBuilder() {
        return new DragPaneLmlActorBuilder();
    }

    @Override
    protected Group getNewInstanceOfGroup(final LmlActorBuilder builder) {
        final DragPaneLmlActorBuilder dragPaneBuilder = (DragPaneLmlActorBuilder) builder;
        final DragPane dragPane = new DragPane(dragPaneBuilder.getGroupType().getGroup());
        dragPane.setDraggable(dragPaneBuilder.getGroupType().getDraggable(dragPane.getGroup()));
        return dragPane;
    }

    @Override
    protected boolean hasComponentActors() {
        return true;
    }

    @Override
    protected Actor[] getComponentActors(final Actor actor) {
        return new Actor[] { ((DragPane) actor).getGroup() };
    }
}
