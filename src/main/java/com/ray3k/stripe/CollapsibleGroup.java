package com.ray3k.stripe;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

/**
 * A layout widget that chooses the largest child to display that can fit in the available space of its given bounds.
 * For example, imagine this widget is set to horizontal and it has two children with minWidths 100 and 200. If the
 * CollapsibleGroup widget's width is set to 300, the child with minWidth 200 is displayed. If the widget's width is set
 * to 150, the child with minWidth 100 is displayed. Widgets too large to display are set to visible = false.
 */
public class CollapsibleGroup extends WidgetGroup {
    public enum CollapseType {
        HORIZONTAL, VERTICAL, BOTH
    }

    private CollapseType collapseType = CollapseType.HORIZONTAL;

    /**
     * The currently visible actor as determined by its minimum size
     **/
    private Actor visibleActor;

    public CollapsibleGroup(CollapseType collapseType) {
        this.collapseType = collapseType;
    }

    public CollapsibleGroup(CollapseType collapseType, Actor... actors) {
        super(actors);
        this.collapseType = collapseType;
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

        switch (collapseType) {
            case HORIZONTAL:
                visibleActor = getFittestHorizontal(getWidth());
                if (visibleActor == null) visibleActor = getSmallestMinWidth();
                break;
            case VERTICAL:
                visibleActor = getFittestVertical(getHeight());
                if (visibleActor == null) visibleActor = getSmallestMinHeight();
                break;
            case BOTH:
                visibleActor = getFittestBoth(getWidth(), getHeight());
                if (visibleActor == null) visibleActor = getSmallestMinSize();
                break;
        }

        if (visibleActor == null) return;
        visibleActor.setVisible(true);
    }

    @Override
    public float getMinWidth() {
        Actor actor = null;
        switch (collapseType) {
            case HORIZONTAL:
            case BOTH:
                actor = getSmallestMinWidth();
                break;
            case VERTICAL:
                actor = getLargestMinWidth();
                break;
        }

        if (actor == null) return 0;
        return actor instanceof Layout ? ((Layout) actor).getMinWidth() : actor.getWidth();
    }

    @Override
    public float getMinHeight() {
        Actor actor = null;
        switch (collapseType) {
            case HORIZONTAL:
                actor = getLargestMinHeight();
                break;
            case VERTICAL:
            case BOTH:
                actor = getSmallestMinHeight();
                break;
        }

        if (actor == null) return 0;
        return actor instanceof Layout ? ((Layout) actor).getMinHeight() : actor.getHeight();
    }

    @Override
    public float getPrefWidth() {
        Actor actor = null;
        switch (collapseType) {
            case HORIZONTAL:
            case BOTH:
                actor = getLargestPrefWidth();
                break;
            case VERTICAL:
                actor = getLargestPrefHeight();
                break;
        }

        if (actor == null) return 0;
        return actor instanceof Layout ? ((Layout) actor).getPrefWidth() : actor.getWidth();
    }

    @Override
    public float getPrefHeight() {
        Actor actor = null;
        switch (collapseType) {
            case HORIZONTAL:
                actor = getLargestPrefWidth();
                break;
            case VERTICAL:
            case BOTH:
                actor = getLargestPrefHeight();
                break;
        }

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

    private Actor getSmallestMinSize() {
        if (getChildren().size == 0) return null;

        float smallestSize = Float.MAX_VALUE;
        Actor smallest = getChildren().first();
        for (Actor actor : getChildren()) {
            float width = actor instanceof Layout ? ((Layout) actor).getMinWidth() : actor.getWidth();
            float height = actor instanceof Layout ? ((Layout) actor).getMinHeight() : actor.getHeight();
            float size = width * height;
            if (size < smallestSize) {
                smallestSize = size;
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

    private Actor getFittestHorizontal(float maxWidth) {
        float largestWidth = -Float.MAX_VALUE;
        Actor largest = null;
        for (Actor actor : getChildren()) {
            float actorWidth = actor instanceof Layout ? ((Layout) actor).getMinWidth() : actor.getWidth();

            if (actorWidth <= maxWidth && actorWidth > largestWidth) {
                largestWidth = actorWidth;
                largest = actor;
            }
        }

        return largest;
    }

    private Actor getFittestVertical(float maxHeight) {
        float largestHeight = -Float.MAX_VALUE;
        Actor largest = null;
        for (Actor actor : getChildren()) {
            float actorHeight = actor instanceof Layout ? ((Layout) actor).getMinHeight() : actor.getHeight();

            if (actorHeight <= maxHeight && actorHeight > largestHeight) {
                largestHeight = actorHeight;
                largest = actor;
            }
        }

        return largest;
    }

    private Actor getFittestBoth(float maxWidth, float maxHeight) {
        float largestWidth = -Float.MAX_VALUE;
        float largestHeight = -Float.MAX_VALUE;
        Actor largest = null;
        for (Actor actor : getChildren()) {
            float actorWidth = actor instanceof Layout ? ((Layout) actor).getMinWidth() : actor.getWidth();
            float actorHeight = actor instanceof Layout ? ((Layout) actor).getMinHeight() : actor.getHeight();

            if (actorWidth <= maxWidth && actorHeight <= maxHeight && actorWidth > largestWidth && actorHeight > largestHeight) {
                largestWidth = actorWidth;
                largestHeight = actorHeight;
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
