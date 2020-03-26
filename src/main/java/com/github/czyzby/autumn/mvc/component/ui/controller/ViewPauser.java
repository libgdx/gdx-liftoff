package com.github.czyzby.autumn.mvc.component.ui.controller;

/** Manages view pausing and resuming.
 *
 * @author MJ */
public interface ViewPauser {
    /** Called each time the game is paused.
     *
     * @param viewController a reference to the controller for utility. */
    public void pause(ViewController viewController);

    /** Called each time the game is resumed.
     *
     * @param viewController a reference to the controller for utility. */
    public void resume(ViewController viewController);
}
