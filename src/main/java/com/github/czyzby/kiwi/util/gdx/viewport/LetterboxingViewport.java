package com.github.czyzby.kiwi.util.gdx.viewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;

/** Combines {@link com.badlogic.gdx.utils.viewport.ScreenViewport screen} and
 * {@link com.badlogic.gdx.utils.viewport.FitViewport fit} viewports functionalities. Similarly to screen viewport,
 * world size is changed on each update (resize), so {@link #update(int, int, boolean)} should be generally called with
 * {@code true} parameter (camera should be centered). Tries to keep the passed aspect ratio by applying letterboxing
 * (horizontal or vertical black bars.
 *
 * <p>
 * On contrary to regular screen viewport, this viewport analyzes screen density (pixel per inch ratio) to preserve
 * correct look on every platform, including mobiles. This is very convenient for GUIs (especially when using fit
 * viewport with the same aspect ratio for game logic rendering), as they will not be scaled when the screen is resized
 * (comparably to using similarly to screen viewport), and yet should still look acceptable on mobile devices
 * (comparably to using fit viewport with a fixed world size).
 *
 * @author MJ */
public class LetterboxingViewport extends ScalingViewport {
    private float scaleX;
    private float scaleY;
    private float targetPpiX;
    private float targetPpiY;
    private float aspectRatio;

    /** Creates a new letterboxing viewport with 4/3 aspect ratio and default target PPI chosen according to the current
     * platform. */
    public LetterboxingViewport() {
        this(GdxUtilities.isMobile() ? 160f : 96f);
    }

    /** Creates a new letterboxing viewport with 4/3 aspect ratio.
     *
     * @param targetPpi this is the targeted pixel per inch ratio, which allows to scale the viewport correctly on
     *            different devices. Usually about 96 for desktop and WebGL platforms, 160 for mobiles. */
    public LetterboxingViewport(final float targetPpi) {
        this(targetPpi, targetPpi, 4f / 3f);
    }

    /** @param targetPpi this is the targeted pixel per inch ratio, which allows to scale the viewport correctly on
     *            different devices. Usually about 96 for desktop and WebGL platforms, 160 for mobiles.
     * @param aspectRatio width divided by height. Will preserve this aspect ratio by applying letterboxing. */
    public LetterboxingViewport(final float targetPpi, final float aspectRatio) {
        this(targetPpi, targetPpi, aspectRatio);
    }

    /** @param targetPpiX this is the targeted pixel per inch ratio on X axis, which allows to scale the viewport
     *            correctly on different devices. Usually about 96 for desktop and WebGL platforms, 160 for mobiles.
     * @param targetPpiY targeted pixel per inch ratio on Y axis. Usually about 96 for desktop and WebGL platforms, 160
     *            for mobiles.
     * @param aspectRatio width divided by height. Will preserve this aspect ratio by applying letterboxing. */
    public LetterboxingViewport(final float targetPpiX, final float targetPpiY, final float aspectRatio) {
        super(Scaling.fit, 0f, 0f); // Temporary setting world size to mock values.
        this.targetPpiX = targetPpiX;
        this.targetPpiY = targetPpiY;
        this.aspectRatio = aspectRatio;
        updateScale();
        updateWorldSize();
    }

    /** Forces update of current pixel per unit ratio according to screen density.
     *
     * @see com.badlogic.gdx.Graphics#getDensity()
     * @see com.badlogic.gdx.Graphics#getPpiX()
     * @see com.badlogic.gdx.Graphics#getPpiY() */
    public void updateScale() {
        scaleX = targetPpiX / Gdx.graphics.getPpiX();
        scaleY = targetPpiY / Gdx.graphics.getPpiY();
    }

    @Override
    public void update(final int screenWidth, final int screenHeight, final boolean centerCamera) {
        updateWorldSize(screenWidth, screenHeight);
        super.update(screenWidth, screenHeight, centerCamera);
    }

    /** Forces update of current world size according to window size. Will try to keep the set aspect ratio. */
    public void updateWorldSize() {
        updateWorldSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /** Forces update of current world size according to window size. Will try to keep the set aspect ratio.
     *
     * @param screenWidth current screen width.
     * @param screenHeight current screen height. */
    private void updateWorldSize(final int screenWidth, final int screenHeight) {
        final float width = screenWidth * scaleX;
        final float height = screenHeight * scaleY;
        final float fitHeight = width / aspectRatio;
        if (fitHeight > height) {
            setWorldSize(height * aspectRatio, height);
        } else {
            setWorldSize(width, fitHeight);
        }
    }

    /** @return the targeted pixel per inch ratio on X axis, which allows to scale the viewport correctly on different
     *         devices. Usually about 96 for desktop and WebGL platforms, 160 for mobiles. */
    public float getTargetPpiX() {
        return targetPpiX;
    }

    /** @param targetPpiX the targeted pixel per inch ratio on X axis, which allows to scale the viewport correctly on
     *            different devices. Usually about 96 for desktop and WebGL platforms, 160 for mobiles. */
    public void setTargetPpiX(final float targetPpiX) {
        this.targetPpiX = targetPpiX;
    }

    /** @return the targeted pixel per inch ratio on Y axis, which allows to scale the viewport correctly on different
     *         devices. Usually about 96 for desktop and WebGL platforms, 160 for mobiles. */
    public float getTargetPpiY() {
        return targetPpiY;
    }

    /** @param targetPpiY the targeted pixel per inch ratio on Y axis, which allows to scale the viewport correctly on
     *            different devices. Usually about 96 for desktop and WebGL platforms, 160 for mobiles. */
    public void setTargetPpiY(final float targetPpiY) {
        this.targetPpiY = targetPpiY;
    }

    /** @return virtual viewport width divided by height. Affects viewport world size during resizing by forcing it to
     *         add letterboxing. Defaults to 4/3. */
    public float getAspectRatio() {
        return aspectRatio;
    }

    /** Allows to directly modify unit per pixel ratio by bypassing PPI check.
     *
     * @param scaleX will be used to multiply screen width to obtain the virtual viewport width during resizing. */
    public void setScaleX(final float scaleX) {
        this.scaleX = scaleX;
    }

    /** Allows to directly modify unit per pixel ratio by bypassing PPI check.
     *
     * @param scaleY will be used to multiply screen height to obtain the virtual viewport height during resizing. */
    public void setScaleY(final float scaleY) {
        this.scaleY = scaleY;
    }

    /** @param aspectRatio virtual viewport width divided by height. Affects viewport world size during resizing by
     *            forcing it to add letterboxing. Defaults to 4/3. */
    public void setAspectRatio(final float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }
}
