package com.github.czyzby.kiwi.util.gdx.scene2d.range;

/** A utility class that contains transition data about a single float value. By holding an alpha value of a color,
 * allows to create fading effects without initiating unnecessary actions. Useful for specific cases (like custom
 * widgets or game logic), for most actors - UI actions are usually less awkward to use.
 *
 * @author MJ */
public class FloatRange {
    public static final float MIN_ALPHA = 0f;
    public static final float MAX_ALPHA = 1f;

    private float initialValue, currentValue, targetValue;
    private float transitionLength;
    private boolean transitionInProgress, isInitialGreater;

    public FloatRange() {
    }

    public FloatRange(final float initialValue) {
        this(initialValue, 0f);
    }

    public FloatRange(final float initialValue, final float transitionLength) {
        this.initialValue = currentValue = targetValue = initialValue;
        this.transitionLength = transitionLength;
    }

    /** Sets target value to 0f. May begin transition. */
    public void setMinAlphaAsTargetValue() {
        setTargetValue(MIN_ALPHA);
    }

    /** Sets target value to 1f. May begin transition. */
    public void setMaxAlphaAsTargetValue() {
        setTargetValue(MAX_ALPHA);
    }

    /** @param transitionLength how long it takes before the range reaches it's destination. */
    public void setTransitionLength(final float transitionLength) {
        this.transitionLength = transitionLength;
    }

    /** @param targetValue if is not equal to previously set target value or current value, will begin transition of
     *            current value. */
    public void setTargetValue(final float targetValue) {
        if (this.targetValue != targetValue) {
            this.targetValue = targetValue;
            initialValue = currentValue;
            transitionInProgress = currentValue != targetValue;
            if (transitionInProgress) {
                isInitialGreater = initialValue > targetValue;
            }
        }
    }

    /** Updates current value as long as it doesn't match the target value.
     *
     * @param delta time passed since the last update. */
    public void update(final float delta) {
        if (transitionInProgress) {
            currentValue += (targetValue - initialValue) * delta / transitionLength;
            if (isInitialGreater) {
                if (currentValue <= targetValue) {
                    finalizeTransition();
                }
            } else {
                if (currentValue >= targetValue) {
                    finalizeTransition();
                }
            }
        }
    }

    private void finalizeTransition() {
        currentValue = targetValue;
        transitionInProgress = false;
    }

    /** @return true if transition is currently in progress. */
    public boolean isTransitionInProgress() {
        return transitionInProgress;
    }

    /** @return current value managed by the range. */
    public float getCurrentValue() {
        return currentValue;
    }

    /** Ends transition. Immediately changes managed current value.
     *
     * @param currentValue all colors will become this value. */
    public void setCurrentValue(final float currentValue) {
        this.currentValue = currentValue;
        initialValue = currentValue;
        targetValue = currentValue;
        transitionInProgress = false;
    }
}
