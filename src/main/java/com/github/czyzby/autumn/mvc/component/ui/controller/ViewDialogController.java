package com.github.czyzby.autumn.mvc.component.ui.controller;

import com.badlogic.gdx.scenes.scene2d.Stage;

/** Manages a single dialog.
 *
 * @author MJ */
public interface ViewDialogController {
    /** @param stage will have the managed dialog shown. */
    public void show(Stage stage);

    /** @return ID of the dialog as it appears in LML views. */
    public String getId();

    /** Destroys the dialog instance, if present. */
    public void destroyDialog();
}
