package com.github.czyzby.autumn.mvc.component.sfx.dto;

import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** Changes sounds state on invocation.
 *
 * @author MJ */
public class ToggleSoundAction implements ActorConsumer<Void, Object> {
    private final MusicService musicService;

    public ToggleSoundAction(final MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public Void consume(final Object actor) {
        musicService.setSoundEnabled(!musicService.isSoundEnabled());
        return null;
    }
}