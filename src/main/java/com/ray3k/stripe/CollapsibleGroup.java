package com.ray3k.stripe;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

/**
 * A layout widget that chooses the largest child to display that can fit in the available space of its given bounds.
 * For example, imagine this widget is set to horizontal = true and it has two children with minWidths 100 and 200. If
 * the CollapsibleGroup widget's width is set to 300, the child with minWidth 200 is displayed. If the widget's width 
 * is set to 150, the child with minWidth 100 is displayed. Widgets too large to display are set to visible = false.
 */
public class CollapsibleGroup extends WidgetGroup {
    /**
     * Set to true if the children's minimum size will be measured horizontally
     **/
    private boolean horizontal;
    /**
     * The currently visible actor as determined by its minimum size
     **/
    private Actor visibleActor;

    public CollapsibleGroup(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public CollapsibleGroup(boolean horizontal, Actor... actors) {
        super(actors);
        this.horizontal = horizontal;
    }

    @Override
    public void layout() {
        super.layout();

        setTouchable(Touchable.childrenOnly);

        for (Actor child : getChildren()) {
            child.setVisible(false);
            child.setPosition(0, 0);
            child.setSize(getWidth(), getHeight());
        }

        visibleActor = getFittest(horizontal, horizontal ? getWidth() : getHeight());
        if (visibleActor == null) visibleActor = horizontal ? getSmallestMinWidth() : getSmallestMinHeight();
        if (visibleActor == null) return;
        visibleActor.setVisible(true);
    }

    @Override
    public float getMinWidth() {
        Actor actor = horizontal ? getSmallestMinWidth() : getLargestMinWidth();
        if (actor == null) return 0;
        return actor instanceof Layout ? ((Layout) actor).getMinWidth() : actor.getWidth();
    }

    @Override
    public float getMinHeight() {
        Actor actor = horizontal ? getLargestMinHeight() : getSmallestMinHeight();
        if (actor == null) return 0;
        return actor instanceof Layout ? ((Layout) actor).getMinHeight() : actor.getHeight();
    }

    @Override
    public float getPrefWidth() {
        Actor actor = horizontal ? getLargestPrefWidth() : getLargestPrefHeight();
        if (actor == null) return 0;
        return actor instanceof Layout ? ((Layout) actor).getPrefWidth() : actor.getWidth();
    }

    @Override
    public float getPrefHeight() {
        Actor actor = horizontal ? getLargestPrefWidth() : getLargestPrefHeight();
        if (actor == null) return 0;
        return actor instanceof Layout ? ((Layout) actor).getPrefHeight() : actor.getHeight();
    }

    private Actor getSmallestMinWidth() {
        if (getChildren().size == 0) return null;

        float smallestWidth = Float.MAX_VALUE;
        Actor smallest = getChildren().first();
        for (Actor actor : getChildren()) {
            float width = actor instanceof Layout ? ((Layout) actor).getMinWidth() : actor.getWidth();
            if (width < smallestWidth) {
                smallestWidth = width;
                smallest = actor;
            }
        }

        return smallest;
    }

    private Actor getSmallestMinHeight() {
        if (getChildren().size == 0) return null;

        float smallestHeight = Float.MAX_VALUE;
        Actor smallest = getChildren().first();
        for (Actor actor : getChildren()) {
            float height = actor instanceof Layout ? ((Layout) actor).getMinHeight() : actor.getHeight();
            if (height < smallestHeight) {
                smallestHeight = height;
                smallest = actor;
            }
        }

        return smallest;
    }

    private Actor getLargestMinWidth() {
        if (getChildren().size == 0) return null;

        float largestWidth = -Float.MAX_VALUE;
        Actor largest = getChildren().first();
        for (Actor actor : getChildren()) {
            float width = actor instanceof Layout ? ((Layout) actor).getMinWidth() : actor.getWidth();
            if (width > largestWidth) {
                largestWidth = width;
                largest = actor;
            }
        }

        return largest;
    }

    private Actor getLargestMinHeight() {
        if (getChildren().size == 0) return null;

        float largestHeight = -Float.MAX_VALUE;
        Actor largest = getChildren().first();
        for (Actor actor : getChildren()) {
            float height = actor instanceof Layout ? ((Layout) actor).getMinHeight() : actor.getHeight();
            if (height > largestHeight) {
                largestHeight = height;
                largest = actor;
            }
        }

        return largest;
    }

    private Actor getSmallestPrefWidth() {
        if (getChildren().size == 0) return null;

        float smallestWidth = Float.MAX_VALUE;
        Actor smallest = getChildren().first();
        for (Actor actor : getChildren()) {
            float width = actor instanceof Layout ? ((Layout) actor).getPrefWidth() : actor.getWidth();
            if (width < smallestWidth) {
                smallestWidth = width;
                smallest = actor;
            }
        }

        return smallest;
    }

    private Actor getSmallestPrefHeight() {
        if (getChildren().size == 0) return null;

        float smallestHeight = Float.MAX_VALUE;
        Actor smallest = getChildren().first();
        for (Actor actor : getChildren()) {
            float height = actor instanceof Layout ? ((Layout) actor).getPrefHeight() : actor.getHeight();
            if (height < smallestHeight) {
                smallestHeight = height;
                smallest = actor;
            }
        }

        return smallest;
    }

    private Actor getLargestPrefWidth() {
        if (getChildren().size == 0) return null;

        float largestWidth = -Float.MAX_VALUE;
        Actor largest = getChildren().first();
        for (Actor actor : getChildren()) {
            float width = actor instanceof Layout ? ((Layout) actor).getPrefWidth() : actor.getWidth();
            if (width > largestWidth) {
                largestWidth = width;
                largest = actor;
            }
        }

        return largest;
    }

    private Actor getLargestPrefHeight() {
        if (getChildren().size == 0) return null;

        float largestHeight = -Float.MAX_VALUE;
        Actor largest = getChildren().first();
        for (Actor actor : getChildren()) {
            float height = actor instanceof Layout ? ((Layout) actor).getPrefHeight() : actor.getHeight();
            if (height > largestHeight) {
                largestHeight = height;
                largest = actor;
            }
        }

        return largest;
    }

    private Actor getFittest(boolean horizontal, float maxLength) {
        float largestLength = -Float.MAX_VALUE;
        Actor largest = null;
        for (Actor actor : getChildren()) {
            float actorLength;
            if (horizontal) actorLength = actor instanceof Layout ? ((Layout) actor).getMinWidth() : actor.getWidth();
            else actorLength = actor instanceof Layout ? ((Layout) actor).getMinHeight() : actor.getHeight();

            if (actorLength <= maxLength && actorLength > largestLength) {
                largestLength = actorLength;
                largest = actor;
            }
        }

        return largest;
    }

    /**
     * The currently visible actor as determined by its minimum size
     **/
    public Actor getVisibleActor() {
        return visibleActor;
    }
}
