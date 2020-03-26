package com.github.czyzby.autumn.mvc.component.ui.controller;

import com.badlogic.gdx.scenes.scene2d.Stage;

/** Manages view rendering.
 *
 * @author MJ */
public interface ViewRenderer {
    /** Called on each application's render class when the view is shown. Should not contain any heavy actions (object
     * creations, asset loading, etc).
     *
     * @param stage managed by the view.
     * @param delta time passed since the last update. */
    void render(Stage stage, float delta);
}
