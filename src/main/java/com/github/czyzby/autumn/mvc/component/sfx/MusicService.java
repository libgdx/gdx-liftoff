package com.github.czyzby.autumn.mvc.component.sfx;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.audio.Sound;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.sfx.dto.CurrentMusicStateAction;
import com.github.czyzby.autumn.mvc.component.sfx.dto.CurrentMusicVolumeAction;
import com.github.czyzby.autumn.mvc.component.sfx.dto.CurrentSoundStateAction;
import com.github.czyzby.autumn.mvc.component.sfx.dto.CurrentSoundVolumeAction;
import com.github.czyzby.autumn.mvc.component.sfx.dto.MusicVolumeChangeAction;
import com.github.czyzby.autumn.mvc.component.sfx.dto.SoundVolumeChangeAction;
import com.github.czyzby.autumn.mvc.component.sfx.dto.ToggleMusicAction;
import com.github.czyzby.autumn.mvc.component.sfx.dto.ToggleSoundAction;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.config.AutumnActionPriority;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.preference.ApplicationPreferences;
import com.github.czyzby.lml.parser.LmlParser;

/** Manages currently played UI theme and sound settings.
 *
 * @author MJ */
public class MusicService {
    /** Name of the action that returns music volume as it appears in LML views. Defaults to "getMusicVolume". */
    public static String GET_MUSIC_VOLUME_ACTION_ID = "getMusicVolume";
    /** Name of the action that returns sound volume as it appears in LML views. Defaults to "getSoundVolume". */
    public static String GET_SOUND_VOLUME_ACTION_ID = "getSoundVolume";
    /** Name of the action that return if music is on as it appears in LML views. Defaults to "musicOn". */
    public static String GET_MUSIC_STATE_ACTION_ID = "musicOn";
    /** Name of the action that return if music is on as it appears in LML views. Defaults to "soundOn". */
    public static String GET_SOUND_STATE_ACTION_ID = "soundOn";

    /** Name of the action that changes music volume as it appears in LML views. Defaults to "setMusicVolume". */
    public static String SET_MUSIC_VOLUME_ACTION_ID = "setMusicVolume";
    /** Name of the action that changes sound volume as it appears in LML views. Defaults to "setSoundVolume". */
    public static String SET_SOUND_VOLUME_ACTION_ID = "setSoundVolume";
    /** Name of the action that turns music on and off as it appears in LML views. Defaults to "toggleMusic". */
    public static String TOGGLE_MUSIC_ACTION_ID = "toggleMusic";
    /** Name of the action that turns sound on and off as it appears in LML views. Defaults to "toggleSound". */
    public static String TOGGLE_SOUND_ACTION_ID = "toggleSound";

    /** Time that has to pass before the theme reaches its full volume or is fully turned off. */
    public static float DEFAULT_THEME_FADING_TIME = 0.25f;

    private static final float MIN_VOLUME = 0f;
    private static final float MAX_VOLUME = 1f;

    private String musicPreferences;
    private String musicVolumePreferenceName;
    private String soundVolumePreferenceName;
    private String musicEnabledPreferenceName;
    private String soundEnabledPreferenceName;

    private float musicVolume = MAX_VOLUME;
    private float soundVolume = MAX_VOLUME;
    private boolean musicEnabled = true;
    private boolean soundEnabled = true;

    private Music currentTheme;

    @Inject private InterfaceService interfaceService;

    private final OnCompletionListener musicCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(final Music music) {
            playCurrentTheme(interfaceService.getCurrentController().getNextTheme());
        }
    };

    @Initiate
    private void initiate() {
        savePreferences(true);

        final LmlParser parser = interfaceService.getParser();
        parser.getData().addActorConsumer(TOGGLE_SOUND_ACTION_ID, new ToggleSoundAction(this));
        parser.getData().addActorConsumer(TOGGLE_MUSIC_ACTION_ID, new ToggleMusicAction(this));
        parser.getData().addActorConsumer(SET_SOUND_VOLUME_ACTION_ID, new SoundVolumeChangeAction(this));
        parser.getData().addActorConsumer(SET_MUSIC_VOLUME_ACTION_ID, new MusicVolumeChangeAction(this));

        parser.getData().addActorConsumer(GET_SOUND_VOLUME_ACTION_ID, new CurrentSoundVolumeAction(this));
        parser.getData().addActorConsumer(GET_MUSIC_VOLUME_ACTION_ID, new CurrentMusicVolumeAction(this));
        parser.getData().addActorConsumer(GET_SOUND_STATE_ACTION_ID, new CurrentSoundStateAction(this));
        parser.getData().addActorConsumer(GET_MUSIC_STATE_ACTION_ID, new CurrentMusicStateAction(this));
    }

    /** @return current volume of music, [0, 1]. */
    public float getMusicVolume() {
        return musicVolume;
    }

    /** @param musicVolume will become current volume of music. If a registered theme is currently playing, it's volume
     *            will be adjusted. */
    public void setMusicVolume(final float musicVolume) {
        this.musicVolume = normalizeVolume(musicVolume);
        if (currentTheme != null) {
            currentTheme.setVolume(musicVolume);
        }
    }

    /** @param musicEnabled true to enable, false to disable. If a current theme is registered, it will stopped or
     *            started according to this setting. */
    public void setMusicEnabled(final boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
        if (currentTheme != null) {
            if (musicEnabled) {
                if (!currentTheme.isPlaying()) {
                    currentTheme.play();
                }
            } else if (currentTheme.isPlaying()) {
                currentTheme.stop();
            }
        }
    }

    /** @return true if music is currently enabled. */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /** @param volume should be normalized.
     * @return float value in range of [0, 1]. */
    public static float normalizeVolume(final float volume) {
        return Math.max(MIN_VOLUME, Math.min(MAX_VOLUME, volume));
    }

    /** Sets the current music volume according to the values stored in preferences. Mostly for internal use.
     *
     * @param preferences path to the preferences. Will be set as global music preferences path.
     * @param preferenceName name of the volume preference.
     * @param defaultValue used if preference is not set. */
    public void setMusicVolumeFromPreferences(final String preferences, final String preferenceName,
            final float defaultValue) {
        musicPreferences = preferences;
        musicVolumePreferenceName = preferenceName;
        setMusicVolume(readFromPreferences(preferences, preferenceName, defaultValue));
    }

    /** Sets the current music state according to the value stored in preferences. Mostly for internal use.
     *
     * @param preferences path to the preferences. Will be set as global music preferences path.
     * @param preferenceName name of the state preference.
     * @param defaultValue used if preference is not set. */
    public void setMusicEnabledFromPreferences(final String preferences, final String preferenceName,
            final boolean defaultValue) {
        musicPreferences = preferences;
        musicEnabledPreferenceName = preferenceName;
        setMusicEnabled(readFromPreferences(preferences, preferenceName, defaultValue));
    }

    /** @return current volume of sound effects. */
    public float getSoundVolume() {
        return soundVolume;
    }

    /** @param soundVolume will become current volume of sound effects. Note that currently played sounds will not be
     *            affected. */
    public void setSoundVolume(final float soundVolume) {
        this.soundVolume = normalizeVolume(soundVolume);
    }

    /** @param soundEnabled true to enable, false to disable. Note that currently played sounds will not be turned
     *            off. */
    public void setSoundEnabled(final boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    /** Sets the current sound state according to the value stored in preferences. Mostly for internal use.
     *
     * @param preferences path to the preferences. Will be set as global music preferences path.
     * @param preferenceName name of the state preference.
     * @param defaultValue used if preference is not set. */
    public void setSoundEnabledFromPreferences(final String preferences, final String preferenceName,
            final boolean defaultValue) {
        musicPreferences = preferences;
        soundEnabledPreferenceName = preferenceName;
        setSoundEnabled(readFromPreferences(preferences, preferenceName, defaultValue));
    }

    /** @return true if sounds are currently enabled. */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /** Sets the current sound volume according to the values stored in preferences. Mostly for internal use.
     *
     * @param preferences path to the preferences. Will be set as global music preferences path.
     * @param preferenceName name of the volume preference.
     * @param defaultValue used if preference is not set. */
    public void setSoundVolumeFromPreferences(final String preferences, final String preferenceName,
            final float defaultValue) {
        musicPreferences = preferences;
        soundVolumePreferenceName = preferenceName;
        setSoundVolume(readFromPreferences(preferences, preferenceName, defaultValue));
    }

    /** Saves music and sound preferences.
     *
     * @param flush if true, preferences will be truly saved - otherwise it just sets them in the preferences map. */
    public void savePreferences(final boolean flush) {
        saveMusicPreferences(flush);
        saveSoundPreferences(flush);
    }

    /** Saves music volume and state in preferences.
     *
     * @param flush if true, preferences will be truly saved - otherwise it just sets them in the preferences map. */
    public void saveMusicPreferences(final boolean flush) {
        if (Strings.isNotEmpty(musicPreferences)) {
            if (Strings.isNotEmpty(musicVolumePreferenceName)) {
                saveInPreferences(musicPreferences, musicVolumePreferenceName, musicVolume);
            }
            if (Strings.isNotEmpty(musicEnabledPreferenceName)) {
                saveInPreferences(musicPreferences, musicEnabledPreferenceName, musicEnabled);
            }
            if (flush) {
                flushPreferences();
            }
        }
    }

    /** Saves sound volume and state in preferences.
     *
     * @param flush if true, preferences will be truly saved - otherwise it just sets them in the preferences map. */
    public void saveSoundPreferences(final boolean flush) {
        if (Strings.isNotEmpty(musicPreferences)) {
            if (Strings.isNotEmpty(soundVolumePreferenceName)) {
                saveInPreferences(musicPreferences, soundVolumePreferenceName, soundVolume);
            }
            if (Strings.isNotEmpty(soundEnabledPreferenceName)) {
                saveInPreferences(musicPreferences, soundEnabledPreferenceName, soundEnabled);
            }
            if (flush) {
                flushPreferences();
            }
        }
    }

    private void flushPreferences() {
        ApplicationPreferences.getPreferences(musicPreferences).flush();
    }

    private static void saveInPreferences(final String preferences, final String preferenceName, final float value) {
        // GWT is not a huge fan of non-string preferences.
        ApplicationPreferences.getPreferences(preferences).putString(preferenceName, String.valueOf(value));
    }

    private static void saveInPreferences(final String preferences, final String preferenceName, final boolean value) {
        // GWT is not a huge fan of non-string preferences.
        ApplicationPreferences.getPreferences(preferences).putString(preferenceName, String.valueOf(value));
    }

    private static float readFromPreferences(final String preferences, final String preferenceName,
            final float defaultValue) {
        return Float.parseFloat(ApplicationPreferences.getPreferences(preferences).getString(preferenceName,
                String.valueOf(defaultValue)));
    }

    private static boolean readFromPreferences(final String preferences, final String preferenceName,
            final boolean defaultValue) {
        return Boolean.parseBoolean(ApplicationPreferences.getPreferences(preferences).getString(preferenceName,
                String.valueOf(defaultValue)));
    }

    /** Restores music and sound settings to the values stored in preferences. Music preferences have to be properly
     * annotated with {@link com.github.czyzby.autumn.mvc.stereotype.preference.sfx.MusicVolume},
     * {@link com.github.czyzby.autumn.mvc.stereotype.preference.sfx.MusicEnabled},
     * {@link com.github.czyzby.autumn.mvc.stereotype.preference.sfx.SoundVolume} and
     * {@link com.github.czyzby.autumn.mvc.stereotype.preference.sfx.SoundEnabled} for each setting to work. */
    public void restoreSettingsFromPreferences() {
        if (Strings.isNotEmpty(musicPreferences)) {
            if (Strings.isNotEmpty(musicVolumePreferenceName)) {
                setMusicVolume(readFromPreferences(musicPreferences, musicVolumePreferenceName, musicVolume));
            }
            if (Strings.isNotEmpty(soundVolumePreferenceName)) {
                setSoundVolume(readFromPreferences(musicPreferences, soundVolumePreferenceName, soundVolume));
            }
            if (Strings.isNotEmpty(musicEnabledPreferenceName)) {
                setMusicEnabled(readFromPreferences(musicPreferences, musicEnabledPreferenceName, musicEnabled));
            }
            if (Strings.isNotEmpty(soundEnabledPreferenceName)) {
                setSoundEnabled(readFromPreferences(musicPreferences, soundEnabledPreferenceName, soundEnabled));
            }
        }
    }

    /** @param sound will be played with the currently set sound volume, provided that sounds are turned on. */
    public void play(final Sound sound) {
        if (soundEnabled) {
            sound.play(soundVolume);
        }
    }

    /** @return currently played music theme, provided that it was properly registered. */
    public Music getCurrentTheme() {
        return currentTheme;
    }

    /** @param currentTheme will be set as the current theme and have its volume changed. If music is enabled, will be
     *            played. */
    public void playCurrentTheme(final Music currentTheme) {
        playCurrentTheme(currentTheme, true);
    }

    /** @param currentTheme will be set as the current theme. If music is enabled, will be played.
     * @param forceVolume if true, music volume will be set to stored preference. */
    public void playCurrentTheme(final Music currentTheme, final boolean forceVolume) {
        this.currentTheme = currentTheme;
        currentTheme.setOnCompletionListener(musicCompletionListener);
        if (forceVolume) {
            currentTheme.setVolume(musicVolume);
        }
        if (musicEnabled) {
            currentTheme.play();
        }
    }

    /** Clears current theme, stopping it from playing. */
    public void clearCurrentTheme() {
        if (currentTheme != null) {
            if (currentTheme.isPlaying()) {
                currentTheme.stop();
            }
            currentTheme = null;
        }
    }

    @Destroy(priority = AutumnActionPriority.VERY_LOW_PRIORITY)
    private void destroy() {
        savePreferences(true);
    }
}