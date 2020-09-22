package com.github.czyzby.kiwi.util.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.InputProcessor;

/** Combines utility of {@link com.badlogic.gdx.ApplicationAdapter} and {@link com.badlogic.gdx.InputAdapter}:
 * implements {@link ApplicationListener} and {@link InputProcessor} with empty methods to limit boilerplate needed to
 * set up an application listener that also listens to user input. Note that by default all methods do nothing.
 *
 * @author MJ */
public class InputAwareApplicationAdapter implements ApplicationListener, InputProcessor {
    @Override
    public void create() {
    }

    @Override
    public void resize(final int width, final int height) {
    }

    @Override
    public void render() {
    }

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
