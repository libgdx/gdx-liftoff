package com.github.czyzby.lml.scene2d.ui.reflected;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;

/** Extends the default {@link Image} with animation functionality. Allows to display multiple {@link Drawable}s one
 * after another in the selected manner. The order of images can be modified with {@link #setBackwards(boolean)} and
 * {@link #setBouncing(boolean)}. If you want the animation to play once and then make the widget work as a regular
 * image, use {@link #setPlayOnce(boolean)}. You can affect the speed of animation with {@link #setDelay(float)} and
 * {@link #setMaxDelay(float)} methods. All standard {@link Image} methods are supported. Does NOT use
 * {@link com.badlogic.gdx.graphics.g2d.Animation} internally, as it relies on texture regions rather than drawables.
 *
 * <p>
 * This class supports pretty simple animations, without frames with different delays etc. If you need complex
 * animations, you might prefer using actions API or a custom actor implementation.
 *
 * @author MJ
 * @see Image */
public class AnimatedImage extends Image {
    private Array<Drawable> frames;
    private int currentFrame;
    private float lastUpdate;
    private float delay = 0.25f;
    private float maxDelay = 1f;
    private boolean bouncing;
    private boolean backwards;
    private boolean playOnce;

    /** @param skin contains drawables.
     * @param frameNames will be converted to drawables from the skin and stored in an {@link Array} used internally by
     *            the image. All frame names have to be valid and present in the skin. */
    public AnimatedImage(final Skin skin, final String... frameNames) {
        this(toFrames(skin, frameNames));
    }

    /** @param frames will be used to construct an {@link Array} of animations's frames. Cannot contain nulls. */
    public AnimatedImage(final Drawable... frames) {
        this(GdxArrays.newArray(frames));
    }

    /** @param frames if not an {@link Array}, will be converted to {@link Array} and used as animation's frames. Cannot
     *            contain nulls. */
    public AnimatedImage(final Iterable<Drawable> frames) {
        this(toArray(frames));
    }

    /** @param frames will be displayed in the chosen manner. Will be used internally by the image. Can be modified
     *            externally, but make sure to call {@link #validateCurrentFrame()} after any modification. Cannot
     *            contain nulls. */
    public AnimatedImage(final Array<Drawable> frames) {
        this.frames = frames;
        if (GdxArrays.isNotEmpty(frames)) {
            setDrawable(frames.first());
        } else if (frames == null) {
            this.frames = GdxArrays.newArray();
        }
    }

    protected static Array<Drawable> toFrames(final Skin skin, final String[] frameNames) {
        final Array<Drawable> frames = GdxArrays.newArray();
        for (final String frame : frameNames) {
            frames.add(skin.getDrawable(frame));
        }
        return frames;
    }

    protected static Array<Drawable> toArray(final Iterable<Drawable> frames) {
        return frames instanceof Array<?> ? (Array<Drawable>) frames : GdxArrays.newArray(frames);
    }

    @Override
    public void act(final float delta) {
        if (frames.size > 1) {
            lastUpdate += Math.min(delta, maxDelay);
            while (lastUpdate >= delay) {
                lastUpdate -= delay;
                updateFrame();
            }
        }
        super.act(delta);
    }

    private void updateFrame() {
        if (backwards) {
            if (--currentFrame < 0) {
                if (playOnce) {
                    currentFrame = 0;
                    final Drawable frame = frames.get(currentFrame);
                    frames = GdxArrays.newArray(frame);
                } else if (bouncing) {
                    currentFrame = Math.min(1, frames.size - 1);
                    backwards = false;
                } else {
                    currentFrame = frames.size - 1;
                }
            }
        } else if (++currentFrame >= frames.size) {
            if (playOnce) {
                final Drawable frame = frames.get(frames.size - 1);
                currentFrame = 0;
                frames = GdxArrays.newArray(frame);
            } else if (bouncing) {
                currentFrame = Math.max(0, frames.size - 2);
                backwards = true;
            } else {
                currentFrame = 0;
            }
        }
        setDrawable(frames.get(currentFrame));
        Gdx.graphics.requestRendering();
    }

    /** @param backwards if true, frames will be iterated over from the end. Note that if the animation is bouncing,
     *            this setting will be overridden. Note that current frame index points to 0 (first) frame at the
     *            beginning; if you want to start with the last frame, make sure to {@link #setCurrentFrame(int)}. */
    public void setBackwards(final boolean backwards) {
        this.backwards = backwards;
    }

    /** @return if true, frames are currently iterated from the end. This value might change if the animation is
     *         bouncing. */
    public boolean isBackwards() {
        return backwards;
    }

    /** @param bouncing if true, the animation will go from the beginning to the end and then from the end to the
     *            beginning - over and over, in an endless loop. */
    public void setBouncing(final boolean bouncing) {
        this.bouncing = bouncing;
    }

    /** @return if true, the animation will go from the beginning to the end and then from the end to the beginning -
     *         over and over, in an endless loop. {@code #isBackwards()} reports the current direction of iteration. */
    public boolean isBouncing() {
        return bouncing;
    }

    /** @param frameId number of the current frame. Index of the drawable in frames array. Can be invalid: will be
     *            clamped. */
    public void setCurrentFrame(final int frameId) {
        if (GdxArrays.isNotEmpty(frames)) {
            currentFrame = MathUtils.clamp(frameId, 0, frames.size - 1);
            setDrawable(frames.get(currentFrame));
        }
    }

    /** Validates current frame index and corrects it, if necessary. Call this method after modifying internal frames
     * array. */
    public void validateCurrentFrame() {
        setCurrentFrame(currentFrame);
    }

    /** @return index of the currently drawn drawable in the frames array. */
    public int getCurrentFrame() {
        return currentFrame;
    }

    /** @return currently used internal array of displayed drawables. Can be shared with other images. Can be modified,
     *         but make sure to call {@link #validateCurrentFrame()} after modification. Cannot contain nulls. */
    public Array<Drawable> getFrames() {
        return frames;
    }

    /** @param frames will become the internally used array of frames displayed by this image. Cannot contain nulls. Can
     *            be shared with other instances. */
    public void setFrames(final Array<Drawable> frames) {
        this.frames = frames;
    }

    /** @param delay the minimum time before the frame is changed to the next one. In seconds. Defaults to 0.2f. If
     *            higher than {@link #getMaxDelay()}, will replace current max delay value. */
    public void setDelay(final float delay) {
        this.delay = delay;
        maxDelay = Math.max(maxDelay, delay);
    }

    /** @return the minimum time before the frame is changed to the next one. In seconds. */
    public float getDelay() {
        return delay;
    }

    /** @param maxDelay current frame index will not be updated more than roughly (maxDelay / delay) frames on a single
     *            {@link #act(float)} call. In seconds. Defaults to 1f. Prevents the widget from lagging during long
     *            rendering delays. */
    public void setMaxDelay(final float maxDelay) {
        this.maxDelay = maxDelay;
    }

    /** @return current frame index will be updated by no more than (min(delta, maxDelay) / delay) frames. In
     *         seconds. */
    public float getMaxDelay() {
        return maxDelay;
    }

    /** @param playOnce if true, this image will replace its internal frames array with a new array containing 1
     *            element: the last frame in the animation. After reaching the last frame, this widget will behave like
     *            a regular image. */
    public void setPlayOnce(final boolean playOnce) {
        this.playOnce = playOnce;
    }

    /** @return if true, this image will replace its internal frames array with a new array containing 1 element: the
     *         last frame in the animation. After reaching the last frame, this widget will behave like a regular
     *         image. */
    public boolean isPlayedOnce() {
        return playOnce;
    }
}
