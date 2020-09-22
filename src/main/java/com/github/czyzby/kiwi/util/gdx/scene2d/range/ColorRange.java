package com.github.czyzby.kiwi.util.gdx.scene2d.range;

import com.badlogic.gdx.graphics.Color;

/** Utility object, meant to be used internally by components that change color over time. Uses simplified
 * interpolation. Useful for specific cases (like custom widgets), for most actors - UI actions are usually less awkward
 * to use.
 *
 * @author MJ */
public class ColorRange {
    private static final float DEFAULT_TRANSITION_LENGTH = 0.25f;

    private final Color initialColor;
    private final Color currentColor;
    private final Color targetColor;
    private float transitionLength;
    // Control variables.
    private boolean transitionInProgress;
    private boolean initialRedIsGreater, initialGreenIsGreater, initialBlueIsGreater, initialAlphaIsGreater;
    private boolean redNeedsUpdate, greenNeedsUpdate, blueNeedsUpdate, alphaNeedsUpdate;

    public ColorRange() {
        this(Color.WHITE, DEFAULT_TRANSITION_LENGTH);
    }

    public ColorRange(final float transitionLength) {
        this(Color.WHITE, transitionLength);
    }

    public ColorRange(final Color initialColor) {
        this(initialColor, DEFAULT_TRANSITION_LENGTH);
    }

    public ColorRange(final Color initialColor, final float transitionLength) {
        this.transitionLength = transitionLength;
        this.initialColor = new Color().set(initialColor);
        currentColor = new Color().set(initialColor);
        targetColor = new Color().set(initialColor);
    }

    /** @param delta time passed since last update. */
    public void update(final float delta) {
        if (transitionInProgress) {
            updateRed(delta);
            updateGreen(delta);
            updateBlue(delta);
            updateAlpha(delta);
            updateTransitionStatus();
        }
    }

    private void updateTransitionStatus() {
        transitionInProgress = redNeedsUpdate || greenNeedsUpdate || blueNeedsUpdate || alphaNeedsUpdate;
    }

    private void updateRed(final float delta) {
        if (redNeedsUpdate) {
            currentColor.r += (targetColor.r - initialColor.r) * delta / transitionLength;
            if (initialRedIsGreater) {
                if (currentColor.r <= targetColor.r) {
                    finalizeRedUpdate();
                }
            } else if (currentColor.r >= targetColor.r) {
                finalizeRedUpdate();
            }
        }
    }

    private void finalizeRedUpdate() {
        currentColor.r = targetColor.r;
        redNeedsUpdate = false;
    }

    private void updateGreen(final float delta) {
        if (greenNeedsUpdate) {
            currentColor.g += (targetColor.g - initialColor.g) * delta / transitionLength;
            if (initialGreenIsGreater) {
                if (currentColor.g <= targetColor.g) {
                    finalizeGreenUpdate();
                }
            } else if (currentColor.g >= targetColor.g) {
                finalizeGreenUpdate();
            }
        }
    }

    private void finalizeGreenUpdate() {
        currentColor.g = targetColor.g;
        greenNeedsUpdate = false;
    }

    private void updateBlue(final float delta) {
        if (blueNeedsUpdate) {
            currentColor.b += (targetColor.b - initialColor.b) * delta / transitionLength;
            if (initialBlueIsGreater) {
                if (currentColor.b <= targetColor.b) {
                    finalizeBlueUpdate();
                }
            } else if (currentColor.b >= targetColor.b) {
                finalizeBlueUpdate();
            }
        }
    }

    private void finalizeBlueUpdate() {
        currentColor.b = targetColor.b;
        blueNeedsUpdate = false;
    }

    private void updateAlpha(final float delta) {
        if (alphaNeedsUpdate) {
            currentColor.a += (targetColor.a - initialColor.a) * delta / transitionLength;
            if (initialAlphaIsGreater) {
                if (currentColor.a <= targetColor.a) {
                    finalizeAlphaUpdate();
                }
            } else if (currentColor.a >= targetColor.a) {
                finalizeAlphaUpdate();
            }
        }
    }

    private void finalizeAlphaUpdate() {
        currentColor.a = targetColor.a;
        alphaNeedsUpdate = false;
    }

    /** @return color that matches current color at the beginning of current (or last) transition. */
    public Color getInitialColor() {
        return initialColor;
    }

    /** @return current color with values between initialColor and targetColor. */
    public Color getCurrentColor() {
        return currentColor;
    }

    /** @return color than will eventually match current color if range is constantly updated. */
    public Color getTargetColor() {
        return targetColor;
    }

    /** @param targetColor begins transition to this color. Sets initial color to current color value. */
    public void setTargetColor(final Color targetColor) {
        if (!targetColor.equals(this.targetColor)) {
            initialColor.set(currentColor);
            this.targetColor.set(targetColor);
            invalidate();
        }
    }

    /** Reschedules color updates. Should be called upon manual color modifications. */
    public void invalidate() {
        if (transitionLength <= 0f) {
            currentColor.set(targetColor);
            redNeedsUpdate = greenNeedsUpdate = blueNeedsUpdate = alphaNeedsUpdate = false;
            transitionInProgress = false;
        } else {
            redNeedsUpdate = initialColor.r != targetColor.r;
            greenNeedsUpdate = initialColor.g != targetColor.g;
            blueNeedsUpdate = initialColor.b != targetColor.b;
            alphaNeedsUpdate = initialColor.a != targetColor.a;

            updateTransitionStatus();
            if (transitionInProgress) {
                initialRedIsGreater = initialColor.r >= targetColor.r;
                initialGreenIsGreater = initialColor.g >= targetColor.g;
                initialBlueIsGreater = initialColor.b >= targetColor.b;
                initialAlphaIsGreater = initialColor.a >= targetColor.a;
            }
        }
    }

    /** @return time that has to pass before current color reaches target color's values. */
    public float getTransitionLength() {
        return transitionLength;
    }

    /** @param transitionLength time that has to pass before current color reaches target color's values. */
    public void setTransitionLength(final float transitionLength) {
        this.transitionLength = transitionLength;
    }

    /** @return true if current color does not match target color. */
    public boolean isTransitionInProgress() {
        return transitionInProgress;
    }
}
