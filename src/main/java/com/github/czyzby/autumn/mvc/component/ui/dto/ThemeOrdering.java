package com.github.czyzby.autumn.mvc.component.ui.dto;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;

/** Determines the way view themes are chosen.
 *
 * @author MJ */
public enum ThemeOrdering {
    /** Chooses a random index. As long as multiple themes are available, never plays the same song twice in a row. */
    RANDOM {
        @Override
        public int getNextIndex(final Array<Music> themes, final int currentIndex) {
            int randomIndex = MathUtils.random(0, GdxArrays.sizeOf(themes) - 1);
            if (currentIndex == randomIndex) {
                randomIndex++;
                randomIndex %= GdxArrays.sizeOf(themes);
            }
            return randomIndex;
        }
    },
    /** Starts with 0, goes through every theme one by one. */
    STANDARD {
        @Override
        public int getNextIndex(final Array<Music> themes, final int currentIndex) {
            return (currentIndex + 1) % GdxArrays.sizeOf(themes);
        }
    },
    /** Starts with 0, goes backwards. */
    REVERSED {
        @Override
        public int getNextIndex(final Array<Music> themes, int currentIndex) {
            currentIndex--;
            if (currentIndex < 0) {
                return GdxArrays.sizeOf(themes) - 1;
            }
            return currentIndex;
        }
    };

    /** @param themes all themes available for the view.
     * @param currentIndex currently used theme.
     * @return next theme index. */
    public abstract int getNextIndex(Array<Music> themes, int currentIndex);
}