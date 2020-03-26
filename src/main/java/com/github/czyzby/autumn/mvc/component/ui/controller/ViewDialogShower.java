package com.github.czyzby.autumn.mvc.component.ui.controller;

import com.badlogic.gdx.scenes.scene2d.ui.Window;

/** Allows to specify actions executed before dialog showing.
 *
 * @author MJ */
public interface ViewDialogShower {
    /** @param dialog is about to be shown. */
    public void doBeforeShow(Window dialog);
}
