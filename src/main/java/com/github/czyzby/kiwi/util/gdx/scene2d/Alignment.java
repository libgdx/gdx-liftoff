package com.github.czyzby.kiwi.util.gdx.scene2d;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IntMap;

/** LibGDX alignments are simple integers and it's rather easy to make a mistake while using the aligning methods. This
 * enums wraps all default alignments, allowing to validate if the alignment value is actually correct. No word
 * separators were used to make constants match (ignoring case) with {@link Align} variables (and for faster LML
 * parsing).
 *
 * @author MJ
 * @see Align */
public enum Alignment {
    /** {@link Align#center} */
    CENTER(Align.center),
    /** {@link Align#top} */
    TOP(Align.top),
    /** {@link Align#bottom} */
    BOTTOM(Align.bottom),
    /** {@link Align#left} */
    LEFT(Align.left),
    /** {@link Align#right} */
    RIGHT(Align.right),
    /** {@link Align#topLeft} */
    TOPLEFT(Align.topLeft),
    /** {@link Align#topRight} */
    TOPRIGHT(Align.topRight),
    /** {@link Align#bottomLeft} */
    BOTTOMLEFT(Align.bottomLeft),
    /** {@link Align#bottomRight} */
    BOTTOMRIGHT(Align.bottomRight);

    private final int alignment;

    private Alignment(final int alignment) {
        this.alignment = alignment;
    }

    /** @return value from LibGDX {@link Align} class represented by this enum's constant. Convenience alias method for
     *         {@link #getAlignment()}. */
    public int get() {
        return alignment;
    }

    /** @return value from LibGDX {@link Align} class represented by this enum's constant. */
    public int getAlignment() {
        return alignment;
    }

    /** @param cell will have its alignment set. */
    public void apply(final Cell<?> cell) {
        cell.align(alignment);
    }

    /** @return true for TOP, TOPLEFT and TOPRIGHT. */
    public boolean isAlignedWithTop() {
        return (alignment & Align.top) != 0;
    }

    /** @return true for BOTTOM, BOTTOMLEFT and BOTTOMRIGHT. */
    public boolean isAlignedWithBottom() {
        return (alignment & Align.bottom) != 0;
    }

    /** @return true for LEFT, BOTTOMLEFT and TOPLEFT. */
    public boolean isAlignedWithLeft() {
        return (alignment & Align.left) != 0;
    }

    /** @return true for RIGHT, BOTTOMRIGHT and TOPRIGHT. */
    public boolean isAlignedWithRight() {
        return (alignment & Align.right) != 0;
    }

    /** @return true for CENTER. */
    public boolean isCentered() {
        return alignment == Align.center;
    }

    /** @param alignment value stored in {@link Align}.
     * @return Alignment enum constant with the same alignment value or null if alignment is invalid. */
    public static Alignment get(final int alignment) {
        return Constants.ALIGNMENTS.get(alignment);
    }

    /** @param alignment value that might be stored in {@link Align}.
     * @return true if the alignment matches an exact value of one of {@link Align} fields. */
    public static boolean isAlignmentValid(final int alignment) {
        return Constants.ALIGNMENTS.containsKey(alignment);
    }

    /** Utility class that allows to initiate static variables with enum's instances.
     *
     * @author MJ */
    private static final class Constants {
        private static final IntMap<Alignment> ALIGNMENTS;

        static {
            ALIGNMENTS = new IntMap<Alignment>(Alignment.values().length);
            for (final Alignment alignment : values()) {
                ALIGNMENTS.put(alignment.alignment, alignment);
            }
        }
    }
}
