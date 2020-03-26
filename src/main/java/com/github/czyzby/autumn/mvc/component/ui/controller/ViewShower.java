package com.github.czyzby.autumn.mvc.component.ui.controller;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;

/** Manages view hiding and showing.
 *
 * @author MJ */
public interface ViewShower {
    /** Called each time the view is shown.
     *
     * @param stage managed by the view.
     * @param action default showing action. Usually does not contain crucial logic (beside setting input processor to
     *            the current stage) and can be ignored. If you want the action to execute after the screen is fully
     *            shown, chain the action with your custom runnables and pass it to the stage. */
    public void show(Stage stage, Action action);

    /** Called each time the view is hidden.
     *
     * @param stage managed by the view.
     * @param action default hiding action. Might contain an action to show the next view, which might have to be
     *            extracted or reproduced. If you want the action to execute after the screen is fully hidden, chain the
     *            action with your custom runnables and pass it to the stage. */
    public void hide(Stage stage, Action action);
}
