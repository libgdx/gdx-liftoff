package com.github.czyzby.lml.vis.ui.reflected;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

/** Mock-up actor class, implementing {@link Layout} and {@link Poolable} for extra utility. Sizes set with
 * {@link #setWidth(float)} and {@link #setHeight(float)} also become the min, preferred and max values.
 *
 * @author MJ */
public class MockUpActor extends Actor implements Layout, Poolable {
    @Override
    public boolean remove() {
        final boolean result = super.remove();
        Pools.free(this);
        return result;
    }

    @Override
    public void layout() {
    }

    @Override
    public void invalidate() {
    }

    @Override
    public void invalidateHierarchy() {
    }

    @Override
    public void validate() {
    }

    @Override
    public void pack() {
    }

    @Override
    public void setFillParent(final boolean fillParent) {
    }

    @Override
    public void setLayoutEnabled(final boolean enabled) {
    }

    @Override
    public float getMinWidth() {
        return getWidth();
    }

    @Override
    public float getMinHeight() {
        return getHeight();
    }

    @Override
    public float getPrefWidth() {
        return getWidth();
    }

    @Override
    public float getPrefHeight() {
        return getHeight();
    }

    @Override
    public float getMaxWidth() {
        return getWidth();
    }

    @Override
    public float getMaxHeight() {
        return getHeight();
    }

    @Override
    public void reset() {
        setStage(null);
        setParent(null);
    }
}