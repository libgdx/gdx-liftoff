package com.github.czyzby.kiwi.util.gdx.file;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.czyzby.kiwi.util.common.UtilitiesClass;

/** Contains formats commonly used in LibGDX applications. Includes dots.
 *
 * @author MJ */
public class CommonFileExtension extends UtilitiesClass {
    private CommonFileExtension() {
    }

    /** Commonly used for configurations, like {@link Skin} data. */
    public static final String JSON = ".json";
    /** Used to represent {@link TextureAtlas}es. */
    public static final String ATLAS = ".atlas";
    /** Common image format. */
    public static final String PNG = ".png";
    /** Common sound format. */
    public static final String OGG = ".ogg";
    /** Common sound format. */
    public static final String WAV = ".wav";
    /** Common sound format. */
    public static final String MP3 = ".mp3";
}
