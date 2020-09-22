package com.github.czyzby.kiwi.util.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

/** Works similarly to {@link com.badlogic.gdx.ApplicationAdapter}, except it clears the screen with black color on
 * render and calls abstract {@link #render(float)} method that you have to override. Except for {@link #render()}, all
 * implemented {@link ApplicationListener} methods are empty and non-final.
 *
 * @author MJ */
public abstract class AbstractApplicationListener implements ApplicationListener {
    @Override
    public void create() {
    }

    @Override
    public void resize(final int width, final int height) {
    }

    @Override
    public final void render() {
        GdxUtilities.clearScreen();
        render(Gdx.graphics.getDeltaTime());
    }

    /** Called after clearing the screen by default {@link #render()} implementation with
     * {@code Gdx.graphics.getDeltaTime()} as parameter.
     *
     * @param deltaTime time passed since the last render call. */
    protected abstract void render(float deltaTime);

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
