package com.ray3k.stripe;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

public class DualCollapsibleGroup extends WidgetGroup {
    /**
     * The currently visible actor as determined by its minimum size
     **/
    private Actor visibleActor;

    @Override
    public void layout() {
        super.layout();

        setTouchable(Touchable.childrenOnly);

        for (Actor child : getChildren()) {
            child.setVisible(false);
            child.setPosition(0, 0);
            child.setSize(getWidth(), getHeight());
        }

        visibleActor = getFittest(getWidth(), getHeight());
        if (visibleActor == null) visibleActor = getSmallestMinSize();
        if (visibleActor == null) return;
        visibleActor.setVisible(true);
    }

    @Override
    public float getMinWidth() {
        Actor actor = getSmallestMinWidth();
        if (actor == null) return 0;
        return actor instanceof Layout ? ((Layout) actor).getMinWidth() : actor.getWidth();
    }

    @Override
    public float getMinHeight() {
        Actor actor = getSmallestMinHeight();
        if (actor == null) return 0;
        return actor instanceof Layout ? ((Layout) actor).getMinHeight() : actor.getHeight();
    }

    @Override
    public float getPrefWidth() {
        Actor actor = getLargestPrefWidth();
        if (actor == null) return 0;
        return actor instanceof Layout ? ((Layout) actor).getPrefWidth() : actor.getWidth();
    }

    @Override
    public float getPrefHeight() {
        Actor actor = getLargestPrefHeight();
        if (actor == null) return 0;
        return actor instanceof Layout ? ((Layout) actor).getPrefHeight() : actor.getHeight();
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

    private Actor getFittest(float maxWidth, float maxHeight) {
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

    public Actor getVisibleActor() {
        return visibleActor;
    }
}
