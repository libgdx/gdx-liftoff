package com.github.czyzby.lml.parser.impl;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.asset.StatefulDisposable;
import com.github.czyzby.lml.parser.LmlView;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.util.LmlUtilities;

/** Abstract base for a LML view. Manages {@link Stage} and lets the view be used as an {@link ActionContainer}.
 * Disposable; calling {@link #dispose()} destroys the stage.
 *
 * @author MJ */
public abstract class AbstractLmlView implements LmlView, ActionContainer, StatefulDisposable {
    private Stage stage;
    private boolean disposed;

    /** @param stage will be filled with actors when the view is passed to a LML parser. Should not be null. */
    public AbstractLmlView(final Stage stage) {
        this.stage = stage;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    /** @param stage will be set as the stage currently used as this view. Should not be null. */
    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    /** Updates and draws {@link Stage}. */
    public void render() {
        stage.act();
        stage.draw();
    }

    /** Updates and draws {@link Stage}.
     *
     * @param delta time passed since last update. */
    public void render(final float delta) {
        stage.act(delta);
        stage.draw();
    }

    /** Updates stage's viewport.
     *
     * @param width new width of the screen.
     * @param height new height of the screen. */
    public void resize(final int width, final int height) {
        stage.getViewport().update(width, height);
    }

    /** Updates stage's viewport.
     *
     * @param width new width of the screen.
     * @param height new height of the screen.
     * @param centerCamera false by default. Some viewports seem to require it to be true for proper behavior (
     *            {@link com.badlogic.gdx.utils.viewport.ScreenViewport}, for example). */
    public void resize(final int width, final int height, final boolean centerCamera) {
        stage.getViewport().update(width, height, centerCamera);
    }

    /** Utility methods that allows to determine actor's ID.
     *
     * @param actor actor parsed from LML template.
     * @return actor's unique ID or null if not set. */
    protected String getActorId(final Actor actor) {
        return LmlUtilities.getActorId(actor);
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }

    /** @param disposed if set to true, {@link #dispose()} will not call {@link Stage#dispose()} method upon invocation.
     *            Set to true if you want to manage stage disposing yourself, but still use view's method for some
     *            reason. Set to false if you "reset" the previously disposed view - this should be generally followed
     *            by setting a new stage with {@link #setStage(Stage)}, as disposed stage often cannot be used. */
    public void setDisposed(final boolean disposed) {
        this.disposed = disposed;
    }

    /** Optional method that returns path to the LML template file that represents this view. This method might return
     * null if file is unknown or not used at all.
     *
     * @return {@link FileHandle} pointing to a .lml file. */
    public FileHandle getTemplateFile() {
        return null;
    }

    /** Optional method that should be called by the view manager when the application is paused. Default implementation
     * is empty. */
    public void pause() {
    }

    /** Optional method that should be called by the view manager when the application is resumed. Default
     * implementation is empty. */
    public void resume() {
    }

    /** Optional method that should be called by the view manager when the view is about to be hidden. */
    public void hide() {
    }

    /** Optional method that should be called by the view manager when the view is about to be shown. */
    public void show() {
    }

    /** Optional method that should be called by the view manager when the view is reloaded. */
    public void clear() {
    }

    @Override
    public void dispose() {
        if (!disposed) {
            Disposables.disposeOf(stage);
            disposed = true;
        }
    }
}
