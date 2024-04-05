package com.ray3k.stripe;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * A layout widget that contains two widgets side by side in horizontal or vertical arrangement. This class aims to give
 * the most space to one widget over the other by even removing the other to make room if necessary.
 * <p>
 * The widget not designated as the smashed widget (defined by boolean smashFirst) is grown to the largest available
 * space. The smashed widget will always be at its pref size or smaller. If the size of the SmashGroup is smaller than
 * the pref size of the primary widget and the min size of the smashed widget, then the smashed widget is no longer
 * rendered. The entire space will then be given to the primary widget.
 */
public class SmashGroup extends WidgetGroup {
    private boolean horizontal;
    private boolean smashFirst;
    private float spacing;
    private Container firstContainer;
    private Container secondContainer;

    public SmashGroup(boolean horizontal) {
        this(null, null, horizontal);
    }

    public SmashGroup(Actor firstActor, Actor secondActor, boolean horizontal) {
        firstContainer = new Container();
        firstContainer.fill();
        addActor(firstContainer);

        secondContainer = new Container();
        secondContainer.fill();
        addActor(secondContainer);

        setFirstActor(firstActor);
        setSecondActor(secondActor);
        this.horizontal = horizontal;
    }

    @Override
    public void layout() {
        super.layout();

        float width = getWidth();
        float height = getHeight();
        Container primaryContainer = smashFirst ? secondContainer : firstContainer;
        Container smashContainer = smashFirst ? firstContainer : secondContainer;
        float lengthTarget = horizontal ? width : height;
        float smashPrefLength = horizontal ? smashContainer.getPrefWidth() : smashContainer.getPrefHeight();
        float smashMinLength = horizontal ? smashContainer.getMinWidth() : smashContainer.getMinHeight();
        float primaryPrefLength = horizontal ? primaryContainer.getPrefWidth() : primaryContainer.getPrefHeight();

        //visibility
        firstContainer.setVisible(true);
        secondContainer.setVisible(true);

        if (primaryPrefLength + spacing + smashMinLength > lengthTarget) smashContainer.setVisible(false);

        //bounds if smaller than minWidth
        if (!smashContainer.isVisible()) {
            primaryContainer.setPosition(0, 0);
            primaryContainer.setSize(width, height);
            primaryContainer.validate();
            return;
        }

        //normal bounds
        float smashLength = primaryPrefLength + spacing + smashPrefLength <= lengthTarget ? smashPrefLength : width - primaryPrefLength - spacing;
        float primaryLength = lengthTarget - spacing - smashLength;
        if (horizontal) {
            primaryContainer.setSize(primaryLength, height);
            smashContainer.setSize(smashLength, height);
        } else {
            primaryContainer.setSize(width, primaryLength);
            smashContainer.setSize(width, smashLength);
        }

        firstContainer.setX(0);
        secondContainer.setY(0);
        secondContainer.setX(horizontal ? firstContainer.getWidth() + spacing : 0);
        firstContainer.setY(horizontal ? 0 : secondContainer.getHeight() + spacing);

        primaryContainer.validate();
        smashContainer.validate();
    }

    @Override
    public float getMinWidth() {
        Container primaryContainer = smashFirst ? secondContainer : firstContainer;
        Container smashContainer = smashFirst ? firstContainer : secondContainer;
        return horizontal ? primaryContainer.getMinWidth() : Math.max(primaryContainer.getMinWidth(), smashContainer.getMinWidth());
    }

    @Override
    public float getMinHeight() {
        Container primaryContainer = smashFirst ? secondContainer : firstContainer;
        Container smashContainer = smashFirst ? firstContainer : secondContainer;
        return horizontal ? Math.max(primaryContainer.getMinHeight(), smashContainer.getMinHeight()) : primaryContainer.getMinHeight();
    }

    @Override
    public float getPrefWidth() {
        return horizontal ? firstContainer.getPrefWidth() + spacing + secondContainer.getPrefWidth() : Math.max(firstContainer.getPrefWidth(), secondContainer.getPrefWidth());
    }

    @Override
    public float getPrefHeight() {
        return horizontal ? Math.max(firstContainer.getPrefHeight(), secondContainer.getPrefHeight()) : firstContainer.getPrefHeight() + spacing + secondContainer.getPrefHeight();
    }

    @Deprecated
    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public boolean isSmashFirst() {
        return smashFirst;
    }

    public void setSmashFirst(boolean smashFirst) {
        this.smashFirst = smashFirst;
    }

    public float getSpace() {
        return spacing;
    }

    public void space(float spacing) {
        this.spacing = spacing;
    }

    public Actor getFirstActor() {
        return firstContainer.getActor();
    }

    public void setFirstActor(Actor firstActor) {
        firstContainer.setActor(firstActor);
    }

    public Actor getSecondActor() {
        return secondContainer.getActor();
    }

    public void setSecondActor(Actor secondActor) {
        secondContainer.setActor(secondActor);
    }
}
