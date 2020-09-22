package com.github.czyzby.kiwi.util.gdx.viewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.kiwi.util.common.UtilitiesClass;
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;

/** Utilities for LibGDX {@link Viewport viewports}.
 *
 * @author MJ
 * @see LetterboxingViewport */
public class Viewports extends UtilitiesClass {
    private Viewports() {
    }

    /** {@link ScreenViewport} is tempting to use - especially for GUIs - as it seems to never scale the assets up or
     * down, preserving the desired look. However, as it quickly turns out, it usually does not work so well on mobile
     * platforms. Fortunately, it provides {@link ScreenViewport#setUnitsPerPixel(float)} method, which allows to apply
     * the appropriate scaling thanks to {@link com.badlogic.gdx.Graphics#getDensity() density} usage. This method tries
     * to choose the correct scaling, depending on the platform.
     *
     * @return a new {@link ScreenViewport} with its unit per pixel ratio adjusted. */
    public static ScreenViewport getDensityAwareViewport() {
        return setDensityAware(new ScreenViewport());
    }

    /** {@link ScreenViewport} is tempting to use - especially for GUIs - as it seems to never scale the assets up or
     * down, preserving the desired look. However, as it quickly turns out, it usually does not work so well on mobile
     * platforms. Fortunately, it provides {@link ScreenViewport#setUnitsPerPixel(float)} method, which allows to apply
     * the appropriate scaling thanks to {@link com.badlogic.gdx.Graphics#getDensity() density} usage. This method tries
     * to choose the correct scaling, depending on the platform.
     *
     * @param viewport its unit per pixel ratio will be adjusted.
     * @return passed viewport (for chaining). */
    public static ScreenViewport setDensityAware(final ScreenViewport viewport) {
        final float density = Gdx.graphics.getDensity();
        final float unitsPerPixel = GdxUtilities.isMobile() ? 1f / density : 96f / 160f / density;
        viewport.setUnitsPerPixel(unitsPerPixel);
        return viewport;
    }

    /** @param stage its viewport will be updated according to current screen size. */
    public static void update(final Stage stage) {
        final Viewport viewport = stage.getViewport();
        update(stage, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                viewport instanceof ScreenViewport || viewport instanceof LetterboxingViewport);
    }

    /** @param stage its viewport will be updated according to passed screen size.
     * @param width current screen width.
     * @param height current screen height. */
    public static void update(final Stage stage, final int width, final int height) {
        final Viewport viewport = stage.getViewport();
        update(stage, width, height, viewport instanceof ScreenViewport || viewport instanceof LetterboxingViewport);
    }

    /** @param stage its viewport will be updated according to passed screen size.
     * @param width current screen width.
     * @param height current screen height.
     * @param centerCamera whether to center camera or not. As a rule of thumb, if the viewport resizes its world size
     *            according to the new screen size ({@link ScreenViewport}, {@link LetterboxingViewport}), its camera
     *            should be centered. Otherwise, pass false. */
    public static void update(final Stage stage, final int width, final int height, final boolean centerCamera) {
        stage.getViewport().update(width, height, centerCamera);
    }

    /** @param stage its viewport camera will be extracted.
     * @return projection matrix, which can be applied to a batch. */
    public static Matrix4 getProjectionMatrix(final Stage stage) {
        return getProjectionMatrix(stage.getViewport());
    }

    /** @param viewport its camera will be extracted.
     * @return projection matrix, which can be applied to a batch. */
    private static Matrix4 getProjectionMatrix(final Viewport viewport) {
        return viewport.getCamera().combined;
    }
}
