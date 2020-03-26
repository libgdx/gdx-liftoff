package com.github.czyzby.autumn.mvc.component.sfx.dto;

import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** Changes music volume on invocation. Expects a slider.
 *
 * @author MJ */
public class MusicVolumeChangeAction implements ActorConsumer<Void, Slider> {
    private final MusicService musicService;

    public MusicVolumeChangeAction(final MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public Void consume(final Slider slider) {
        musicService.setMusicVolume(slider.getValue());
        return null;
    }
}