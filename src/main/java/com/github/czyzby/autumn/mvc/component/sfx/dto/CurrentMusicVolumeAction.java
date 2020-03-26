package com.github.czyzby.autumn.mvc.component.sfx.dto;

import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** Returns current music volume on invocation.
 *
 * @author MJ */
public class CurrentMusicVolumeAction implements ActorConsumer<Float, Object> {
    private final MusicService musicService;

    public CurrentMusicVolumeAction(final MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public Float consume(final Object actor) {
        return musicService.getMusicVolume();
    }
}