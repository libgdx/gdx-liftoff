package com.github.czyzby.autumn.mvc.component.sfx.dto;

import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** Returns current music state on invocation.
 *
 * @author MJ */
public class CurrentMusicStateAction implements ActorConsumer<Boolean, Object> {
    private final MusicService musicService;

    public CurrentMusicStateAction(final MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public Boolean consume(final Object actor) {
        return musicService.isMusicEnabled();
    }
}