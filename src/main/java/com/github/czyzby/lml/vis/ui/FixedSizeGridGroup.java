
package com.github.czyzby.lml.vis.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.Pools;
import com.github.czyzby.lml.vis.ui.reflected.MockUpActor;
import com.kotcrab.vis.ui.layout.DragPane;
import com.kotcrab.vis.ui.layout.DragPane.DefaultDragListener;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.Draggable;

/** A specialized {@link GridGroup} with fixed amount of children. Contains mock-up actors in "empty" cells. Created as
 * a {@link GridGroup} replacement for {@link DragPane} usage - this might be very useful to display RPG-style
 * inventories, for example. Instead of simply allowing to move the actors around the group, its drag listener swaps
 * dragged cells, allowing to fully customize actor layout.
 *
 * @author MJ
 * @see #getDraggable(FixedSizeGridGroup)
 * @see GridDragListener */
public class FixedSizeGridGroup extends GridGroup {
    private final IntSet blockedIndexes = new IntSet();
    private SwapListener swapListener;
    private int itemsAmount;

    /** @param itemsAmount amount of cells expected in the grid. All these cells will be filled with mock-up actors.
     * @param itemSize width and height of individual cells. */
    public FixedSizeGridGroup(final int itemsAmount, final int itemSize) {
        this(itemsAmount, itemSize, itemSize);
    }

    /** @param itemsAmount amount of cells expected in the grid. All these cells will be filled with mock-up actors.
     * @param itemWidth width of individual cells.
     * @param itemHeight height of individual cells. */
    public FixedSizeGridGroup(final int itemsAmount, final int itemWidth, final int itemHeight) {
        setItemWidth(itemWidth);
        setItemHeight(itemHeight);
        setSpacing(0f);
        this.itemsAmount = itemsAmount;
        fillCellsWithMockUps();
    }

    private void fillCellsWithMockUps() {
        for (int index = 0; index < itemsAmount; index++) {
            super.addActor(getMockUpActor());
        }
    }

    /** @param index child cell with this index will reject dragged actors. */
    public void setBlockedIndex(final int index) {
        blockedIndexes.add(index);
    }

    /** @param indexes child cells with this indexes will reject dragged actors. */
    public void setBlockedIndexes(final int... indexes) {
        blockedIndexes.addAll(indexes);
    }

    /** @param index a child index in the grid.
     * @return true if index cannot be filled with a non-mock-up actor. */
    public boolean isIndexBlocked(final int index) {
        return blockedIndexes.contains(index);
    }

    /** @return direct reference to internal set of blocked indexes. */
    public IntSet getBlockedIndexes() {
        return blockedIndexes;
    }

    /** @return fixed amount of grid's cells. */
    public int getItemsAmount() {
        return itemsAmount;
    }

    /** @param itemsAmount fixed amount of grid's cells. If smaller than current items amount, some cells will be
     *            removed. If higher than current items amount, new cells with mock-up actors will be added. */
    public void setItemsAmount(final int itemsAmount) {
        if (this.itemsAmount < itemsAmount) {
            for (; this.itemsAmount < itemsAmount; this.itemsAmount++) {
                super.addActor(getMockUpActor());
            }
        } else if (this.itemsAmount > itemsAmount) {
            for (; this.itemsAmount > itemsAmount; this.itemsAmount--) {
                super.removeActor(getChildren().get(this.itemsAmount - 1), true);
            }
        }
    }

    /** @param result will contain all non-mock-up actors stored in the group.
     * @return passed array with non-mock-up items. */
    public Array<Actor> getItems(final Array<Actor> result) {
        for (final Actor actor : getChildren()) {
            if (!isMockUp(actor)) {
                result.add(actor);
            }
        }
        return result;
    }

    /** @return true if grid stores no mock-up actors (all its cells are filled). */
    public boolean isFull() {
        final Array<Actor> children = getChildren();
        for (int index = 0, size = children.size; index < size; index++) {
            if (!isIndexBlocked(index) && isMockUp(children.get(index))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void clear() {
        super.clear();
        fillCellsWithMockUps();
    }

    @Override
    public boolean removeActor(final Actor actor, final boolean unfocus) {
        final int index = getChildren().indexOf(actor, true);
        if (index >= 0) {
            super.removeActor(actor, unfocus);
            if (getChildren().size < itemsAmount) {
                super.addActorAt(index, getMockUpActor());
            }
            return true;
        }
        return false;
    }

    /** @param actor will be added to the first cell which currently contains a mock-up actor and is not blocked. Be
     *            careful: if no mock-ups are found, will replace last actor in the group.
     * @see #setBlockedIndex(int)
     * @see #isFull() */
    @Override
    public void addActor(final Actor actor) {
        super.addActor(actor); // Has to be done to set parent and stage.
        final Object[] children = getChildren().items;
        children[itemsAmount] = null; // Removing the added actor.
        getChildren().size--;
        for (int index = 0; index < itemsAmount; index++) {
            if (!isIndexBlocked(index) && isMockUp(children[index])) {
                free(children[index]);
                children[index] = actor;
                childrenChanged();
                return;
            }
        }
        // No mock-ups found - replacing last cell:
        children[itemsAmount - 1] = actor;
        childrenChanged();
    }

    @Override
    @Deprecated
    public void addActorAfter(final Actor actorAfter, final Actor actor) {
        super.addActorAfter(actorAfter, actor);
        getChildren().get(itemsAmount).remove(); // Removing last cell.
    }

    @Override
    @Deprecated
    public void addActorBefore(final Actor actorBefore, final Actor actor) {
        super.addActorBefore(actorBefore, actor);
        getChildren().get(itemsAmount).remove(); // Removing last cell.
    }

    @Override
    public void addActorAt(final int index, final Actor actor) {
        if (isIndexBlocked(index)) {
            throw new IllegalStateException("Cannot add actor to a cell with blocked index: " + index);
        }
        super.addActor(actor); // Has to be done to set parent and stage.
        final Object[] children = getChildren().items;
        children[itemsAmount] = null; // Removing the added actor.
        getChildren().size--;
        if (isMockUp(children[index])) {
            free(children[index]);
        }
        children[index] = actor;
        childrenChanged();
    }

    /** @return a new (or pooled) instance of mock-up actor. */
    protected Actor getMockUpActor() {
        return Pools.obtain(MockUpActor.class);
    }

    /** @param mockUpActor is no longer needed. If actor instances are pooled, it should be returned to the pool. */
    protected void free(final Object mockUpActor) {
        Pools.free(mockUpActor);
    }

    /** @param actor a possible mock-up actor stored in a cell.
     * @return true if passed actor is a mock-up. */
    public boolean isMockUp(final Object actor) {
        return actor instanceof MockUpActor;
    }

    /** @return listener notified of actor swaps. Can be null. */
    public SwapListener getSwapListener() {
        return swapListener;
    }

    /** @param swapListener will be notified of actor swaps. Pass null to remove. */
    public void setSwapListener(final SwapListener swapListener) {
        this.swapListener = swapListener;
    }

    /** @param group is about to be added to a {@link DragPane} and needs a {@link Draggable} for its children.
     * @return a specialized {@link Draggable} for {@link FixedSizeGridGroup}. */
    public static Draggable getDraggable(final FixedSizeGridGroup group) {
        return new Draggable(new GridDragListener(group));
    }

    /** {@link DefaultDragListener} customized for {@link FixedSizeGridGroup}. Allows to move children only to other
     * {@link DragPane}s storing instances of {@link FixedSizeGridGroup}s. Instead of simply moving the actors around
     * the group, this listener swaps children positions.
     *
     * @author MJ */
    public static class GridDragListener extends DefaultDragListener {
        private final FixedSizeGridGroup group;

        /** @param group will be managed by this listener. */
        public GridDragListener(final FixedSizeGridGroup group) {
            this.group = group;
        }

        @Override
        public boolean onStart(final Draggable draggable, final Actor actor, final float stageX, final float stageY) {
            if (group.isMockUp(actor)) {
                return CANCEL;
            }
            return super.onStart(draggable, actor, stageX, stageY);
        }

        @Override
        protected boolean addDirectlyToPane(final Draggable draggable, final Actor actor, final DragPane dragPane) {
            return CANCEL; // Prohibited. Can only swap children.
        }

        @Override
        protected boolean accept(final Actor actor, final DragPane dragPane) {
            return dragPane != null && dragPane.getGroup() instanceof FixedSizeGridGroup
                    && super.accept(actor, dragPane);
        }

        @Override
        protected boolean addToHorizontalGroup(final Actor actor, final DragPane dragPane,
                final Actor directPaneChild) {
            return CANCEL;
        }

        @Override
        protected boolean addToVerticalGroup(final Actor actor, final DragPane dragPane, final Actor directPaneChild) {
            return CANCEL;
        }

        @Override
        protected boolean addToFloatingGroup(final Draggable draggable, final Actor actor, final DragPane dragPane) {
            return CANCEL;
        }

        @Override
        protected boolean addToOtherGroup(final Actor actor, final DragPane dragPane, final Actor directPaneChild) {
            final int actorIndex = group.getChildren().indexOf(actor, true);
            int childIndex = group.getChildren().indexOf(directPaneChild, true);
            if (childIndex >= 0) { // Same group:
                if (group.isIndexBlocked(childIndex)
                        || !hasSwapListenerApproval(group, actor, group, directPaneChild)) {
                    return CANCEL;
                }
                final Object[] children = group.getChildren().items;
                children[actorIndex] = directPaneChild;
                children[childIndex] = actor;
                group.childrenChanged();
            } else { // Dragged into a different group:
                if (dragPane.getGroup() instanceof FixedSizeGridGroup) {
                    childIndex = dragPane.getGroup().getChildren().indexOf(directPaneChild, true);
                    final FixedSizeGridGroup targetGroup = (FixedSizeGridGroup) dragPane.getGroup();
                    if (targetGroup.isIndexBlocked(childIndex)
                            || !isSwapApproved(group, actor, targetGroup, directPaneChild)) {
                        return CANCEL;
                    }
                    dragPane.addActorAt(childIndex, actor);
                    group.getParent().addActorAt(actorIndex, directPaneChild); // Replaces Draggable listener.
                } else { // Not expected, dragged into a non-GridGroup:
                    return CANCEL;
                }
            }
            return APPROVE;
        }

        private static boolean isSwapApproved(final FixedSizeGridGroup fromGroup, final Actor removedActor,
                final FixedSizeGridGroup toGroup, final Actor addedActor) {
            return hasSwapListenerApproval(fromGroup, removedActor, toGroup, addedActor)
                    && hasSwapListenerApproval(toGroup, addedActor, fromGroup, removedActor);
        }

        private static boolean hasSwapListenerApproval(final FixedSizeGridGroup fromGroup, final Actor removedActor,
                final FixedSizeGridGroup toGroup, final Actor addedActor) {
            return fromGroup.getSwapListener() == null
                    || fromGroup.getSwapListener().onSwap(fromGroup, removedActor, toGroup, addedActor);
        }

        /** @param previousParent previous parent of the dragged actor. Can be null.
         * @param previousIndex previous cell index of the dragged actor. -1 if has no parent.
         * @param removedCell this actor was previously in a cell to which another actor was dragged and dropped. By
         *            default, this method will replace the previous cell of the other group with the removed actor, if
         *            the group is also a {@link FixedSizeGridGroup}. */
        protected void onNonEmptyCellReplacement(final Group previousParent, final int previousIndex,
                final Actor removedCell) {
            if (previousParent instanceof FixedSizeGridGroup) {
                previousParent.addActorAt(previousIndex, removedCell);
            }
        }
    }

    /** Allows to manage swapping events.
     *
     * @author MJ */
    public static interface SwapListener {
        /** Use in listener's method for code clarity. */
        boolean CANCEL = false, APPROVE = true;

        /** Note that if an item is moved from one group to another and both groups have listeners, both of them will be
         * invoked and both of them have to approve. If an item is moved around internally (in the same group, moved
         * from one cell to another), listener will be invoked once.
         *
         * @param fromGroup has the listener attached.
         * @param removedActor will be removed from the fromGroup and added to toGroup.
         * @param toGroup will contain the removed actor. Might be the same object as fromGroup.
         * @param addedActor will be added in place of removedActor to fromGroup. Can be a mock-up object.
         * @return true if you want to accept the swap. False if you want to cancel it.
         * @see #CANCEL
         * @see #APPROVE
         * @see FixedSizeGridGroup#isMockUp(Object) */
        boolean onSwap(FixedSizeGridGroup fromGroup, Actor removedActor, FixedSizeGridGroup toGroup, Actor addedActor);
    }
}
