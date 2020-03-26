package com.github.czyzby.autumn.mvc.component.ui.dto;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;
import com.github.czyzby.autumn.mvc.component.ui.controller.impl.AnnotatedViewController;

/** Utility action executed after all themes are loaded.
 *
 * @author MJ */
public class ThemesAssignmentAction implements Runnable {
    private final String[] themes;
    private final AnnotatedViewController viewConroller;
    private final AssetService assetService;

    public ThemesAssignmentAction(final String[] themes, final AnnotatedViewController viewConroller,
            final AssetService assetService) {
        this.themes = themes;
        this.viewConroller = viewConroller;
        this.assetService = assetService;
    }

    @Override
    public void run() {
        final Array<Music> musicThemes = viewConroller.getThemes();
        for (final String theme : themes) {
            musicThemes.add(assetService.get(theme, Music.class));
        }
    }
}
