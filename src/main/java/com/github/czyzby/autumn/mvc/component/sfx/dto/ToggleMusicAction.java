package com.github.czyzby.autumn.mvc.component.sfx.dto;

import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** Changes music state on invocation.
 *
 * @author MJ */
public class ToggleMusicAction implements ActorConsumer<Void, Object> {
    private final MusicService musicService;

    public ToggleMusicAction(final MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public Void consume(final Object actor) {
        musicService.setMusicEnabled(!musicService.isMusicEnabled());
        return null;
    }
}