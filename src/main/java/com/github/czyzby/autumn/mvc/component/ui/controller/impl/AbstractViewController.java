package com.github.czyzby.autumn.mvc.component.ui.controller.impl;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewController;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.lml.parser.action.ActionContainer;

/** Base class for a view that does not use LML, for whatever reason.
 *
 * @author MJ */
public abstract class AbstractViewController implements ViewController {
    protected Stage stage;

    @Override
    public void createView(final InterfaceService interfaceService) {
        stage = new Stage(interfaceService.getViewportProvider().provide());
        createView(interfaceService, stage);
    }

    /** Called on screen creation.
     *
     * @param interfaceService manages views.
     * @param stage is initiated with the default viewport type. */
    protected abstract void createView(InterfaceService interfaceService, Stage stage);

    @Override
    public void destroyView() {
        Disposables.disposeOf(stage);
        stage = null;
    }

    @Override
    public boolean isCreated() {
        return stage != null;
    }

    @Override
    public void render(final float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(final int width, final int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show(final Action action) {
        stage.addAction(action);
    }

    @Override
    public void hide(final Action action) {
        stage.addAction(action);
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public ActionContainer getActionContainer() {
        if (this instanceof ActionContainer) {
            return (ActionContainer) this;
        }
        return null;
    }

    @Override
    public boolean isFirst() {
        return false;
    }
}
