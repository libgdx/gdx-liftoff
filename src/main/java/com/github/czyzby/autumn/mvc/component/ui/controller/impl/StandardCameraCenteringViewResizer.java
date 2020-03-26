package com.github.czyzby.autumn.mvc.component.ui.controller.impl;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewResizer;

/** Resizes the screen by updating stage viewport and centering the camera.
 *
 * @author MJ
 * @deprecated {@link StandardViewResizer} now properly detects how the viewport should be updated. Use it where
 *             possible. */
@Deprecated
public class StandardCameraCenteringViewResizer implements ViewResizer {
    @Override
    public void resize(final Stage stage, final int width, final int height) {
        stage.getViewport().update(width, height, true);
    }
}
