package com.github.czyzby.autumn.mvc.component.ui.action;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.github.czyzby.autumn.mvc.component.sfx.MusicService;

/** Utility action that slowly changes the volume of music.
 *
 * @author MJ */
public class MusicFadingAction extends Action {
    private Music music;
    private float totalVolumeDifference, time, targetValue;
    private boolean gettingLouder;

    @Override
    public boolean act(final float delta) {
        music.setVolume(MusicService.normalizeVolume(music.getVolume() + totalVolumeDifference * delta / time));
        if (gettingLouder) {
            if (music.getVolume() >= targetValue) {
                music.setVolume(targetValue);
                return true;
            }
        } else if (music.getVolume() <= targetValue) {
            music.setVolume(targetValue);
            return true;
        }
        return false;
    }

    private void setData(final Music music, final float time, final float target) {
        this.music = music;
        this.time = time;
        targetValue = target;
        totalVolumeDifference = target - music.getVolume();
        gettingLouder = music.getVolume() < target;
    }

    /** @param music volume will be set to zero and slowly faded in to the target.
     * @param time fading duration.
     * @param volume volume target.
     * @return action that fades in the music. */
    public static Action fadeIn(final Music music, final float time, final float volume) {
        final MusicFadingAction action = Actions.action(MusicFadingAction.class);
        music.setVolume(0f);
        action.setData(music, time, volume);
        return action;
    }

    /** @param music volume will be slowly faded to 0.
     * @param time fading duration.
     * @return action that fades out the music. */
    public static Action fadeOut(final Music music, final float time) {
        final MusicFadingAction action = Actions.action(MusicFadingAction.class);
        action.setData(music, time, 0f);
        return action;
    }
}
