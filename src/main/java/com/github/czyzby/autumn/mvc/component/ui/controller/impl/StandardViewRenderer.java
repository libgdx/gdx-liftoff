package com.github.czyzby.autumn.mvc.component.ui.controller.impl;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;

/** Renders the view by invoking stage acting and rendering it.
 *
 * @author MJ */
public class StandardViewRenderer implements ViewRenderer {
    @Override
    public void render(final Stage stage, final float delta) {
        stage.act(delta);
        stage.draw();
    }
}
