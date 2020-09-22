package com.github.czyzby.kiwi.util.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/** Combines utility of {@link com.badlogic.gdx.ApplicationAdapter} and {@link com.badlogic.gdx.InputAdapter}:
 * implements {@link ApplicationListener} and {@link InputProcessor} with mostly empty methods to limit boilerplate
 * needed to set up an application listener that also listens to user input. This is basically an updated version of
 * {@link InputAwareApplicationAdapter}.
 *
 * <p>
 * {@link #create()} method invokes {@link #initiate()} and then sets this listener as application's main
 * {@link InputProcessor}. Similarly to {@link AbstractApplicationListener}, {@link #render(float)} is provided: it is
 * automatically called by {@link #render()} with time passed since the last render (in seconds) right after clearing
 * the screen with black color.
 *
 * @author MJ */
public abstract class InputAwareApplicationListener implements ApplicationListener, InputProcessor {
    @Override
    public void create() {
        initiate();
        Gdx.input.setInputProcessor(this);
    }

    /** Invoked by {@link #create()} method before setting this listener as application's {@link InputProcessor}. */
    protected abstract void initiate();

    @Override
    public void resize(final int width, final int height) {
    }

    @Override
    public void render() {
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

    @Override
    public boolean keyDown(final int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(final int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(final char character) {
        return false;
    }

    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(final int amount) {
        return false;
    }
}
