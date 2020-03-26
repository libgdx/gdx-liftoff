package com.github.czyzby.lml.vis.parser.impl.tag.builder;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.vis.ui.FixedSizeGridGroup;
import com.kotcrab.vis.ui.layout.FloatingGroup;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import com.kotcrab.vis.ui.layout.VerticalFlowGroup;
import com.kotcrab.vis.ui.widget.Draggable;

/** Allows to build drag pane widgets.
 *
 * @author MJ */
public class DragPaneLmlActorBuilder extends LmlActorBuilder {
    private GroupType groupType = GroupType.HORIZONTAL;

    /** @return type of group managed by the drag pane. */
    public GroupType getGroupType() {
        return groupType;
    }

    /** @param groupType type of group managed by the drag pane. */
    public void setGroupType(final GroupType groupType) {
        this.groupType = groupType;
    }

    /** Contains all default group types managed by the drag pane. When referenced in drag pane type attribute, has to
     * match the exact enum name (case ignored).
     *
     * @author MJ
     * @see com.github.czyzby.lml.vis.parser.impl.attribute.building.GroupTypeLmlAttribute */
    public static enum GroupType {
        /** Constructs {@link HorizontalGroup}. */
        HORIZONTAL {
            @Override
            public WidgetGroup getGroup() {
                return new HorizontalGroup();
            }
        },
        /** Constructs {@link VerticalGroup}. */
        VERTICAL {
            @Override
            public WidgetGroup getGroup() {
                return new VerticalGroup();
            }
        },
        /** Constructs {@link GridGroup}. */
        GRID {
            @Override
            public WidgetGroup getGroup() {
                return new GridGroup();
            }
        },
        /** Constructs {@link FixedSizeGridGroup}. */
        FIXED {
            @Override
            public WidgetGroup getGroup() {
                return new FixedSizeGridGroup(16, 32);
            }

            @Override
            public Draggable getDraggable(final WidgetGroup group) {
                return FixedSizeGridGroup.getDraggable((FixedSizeGridGroup) group);
            }
        },
        /** Constructs {@link HorizontalFlowGroup}. */
        HFLOW {
            @Override
            public WidgetGroup getGroup() {
                return new HorizontalFlowGroup();
            }
        },
        /** Constructs {@link VerticalFlowGroup}. */
        VFLOW {
            @Override
            public WidgetGroup getGroup() {
                return new VerticalFlowGroup();
            }
        },
        /** Constructs {@link FloatingGroup}. */
        FLOATING {
            @Override
            public WidgetGroup getGroup() {
                return new FloatingGroup();
            }
        };

        /** @return a new instance of selected group type. */
        public abstract WidgetGroup getGroup();

        /** @param group an instance of selected group, see {@link #getGroup()}.
         * @return a customized instance of {@link Draggable} for this group type. */
        public Draggable getDraggable(final WidgetGroup group) {
            return new Draggable();
        }
    }
}
