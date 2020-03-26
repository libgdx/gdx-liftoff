package com.github.czyzby.autumn.mvc.component.ui.controller;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.lml.parser.LmlView;
import com.github.czyzby.lml.parser.action.ActionContainer;

/** Manages a single view.
 *
 * @author MJ */
public interface ViewController extends LmlView {
    /** Should fully view's object. This generally invokes filling {@link Stage} with
     * referenced LML template. This action might be called multiple times, as screens are sometimes reloaded.
     *
     * @param interfaceService initiates view creation.
     * @see InterfaceService#getParser() */
    void createView(InterfaceService interfaceService);

    /** Should destroy the manage view. Note that this action might be called multiple times, as screens are sometimes
     * reloaded - be careful with asset unloading. */
    void destroyView();

    /** @return true if the view was constructed with {@link #createView(InterfaceService)} method and is ready to be
     *         shown. */
    boolean isCreated();

    /** Draws the view.
     *
     * @param delta time passed since the last update. */
    void render(float delta);

    /** Resizes the managed view.
     *
     * @param width new screen width.
     * @param height new screen height. */
    void resize(int width, int height);

    /** Pauses the view. */
    void pause();

    /** Resumes the view. */
    void resume();

    /** Shows the view.
     *
     * @param action provided by the view's manager. Should be executed to show the view, as it might contain chained
     *            actions. */
    void show(Action action);

    /** Hides the view.
     *
     * @param action provided by the view's manager. Should be executed to hide the view, as it might contain chained
     *            actions. */
    void hide(Action action);

    /** @return Scene2D stage managed by the controller. */
    @Override
    Stage getStage();

    /** @return if the controller is an action container (or contains one), this will be the name of the container
     *         recognized by the view. This is also the name of the view used for screen transition from within the LML
     *         templates.
     * @see com.github.czyzby.autumn.mvc.component.ui.action.ScreenTransitionAction */
    @Override
    String getViewId();

    /** @return action container passed to the LML parser, available in views. */
    ActionContainer getActionContainer();

    /** @return true if the controller manages application's first screen. */
    boolean isFirst();

    /** @return music themes played during the view is shown. */
    Array<Music> getThemes();

    /** @return next screen's theme, according to the chosen theme ordering. */
    Music getNextTheme();
}
