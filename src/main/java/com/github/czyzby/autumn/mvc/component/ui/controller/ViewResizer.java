package com.github.czyzby.autumn.mvc.component.ui.controller;

import com.badlogic.gdx.scenes.scene2d.Stage;

/** Manages view resizing.
 *
 * @author MJ */
public interface ViewResizer {
    /** Called each time the window is resized.
     *
     * @param stage managed by the view.
     * @param width new screen width.
     * @param height new screen height. */
    void resize(Stage stage, int width, int height);
}
