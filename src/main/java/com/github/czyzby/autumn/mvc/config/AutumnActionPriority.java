package com.github.czyzby.autumn.mvc.config;

/** Contains priorities used by initiation and destruction methods in Autumn MVC.
 *
 * @author MJ */
public class AutumnActionPriority {
    /** Can be extended to contain all application's priorities, but should not be initiated. */
    protected AutumnActionPriority() {
    }

    /** 3. Executes first. Used by: {@link com.github.czyzby.autumn.mvc.component.ui.SkinService} (skins initiation and
     * assignment). */
    public static final int TOP_PRIORITY = 3;
    /** 2. Used by: {@link com.github.czyzby.autumn.mvc.component.ui.InterfaceService} (bundles and preferences
     * assignment, LML parser creation). */
    public static final int VERY_HIGH_PRIORITY = 2;
    /** 1. Used by: {@link com.github.czyzby.autumn.mvc.component.preferences.PreferencesService} (preferences loading),
     * {@link com.github.czyzby.autumn.mvc.component.ui.processor.LmlMacroAnnotationProcessor} (macros loading). */
    public static final int HIGH_PRIORITY = 1;
    /** 0. Used by: {@link com.github.czyzby.autumn.mvc.component.sfx.MusicService} (adding sound settings actions to
     * LML parser). */
    public static final int DEFAULT_PRIORITY = 0;
    /** -1. Used by: {@link com.github.czyzby.autumn.mvc.component.ui.InterfaceService} (controllers destruction, batch
     * disposing, parser destruction). */
    public static final int LOW_PRIORITY = -1;
    /** -2. Used by: {@link com.github.czyzby.autumn.mvc.component.sfx.MusicService} (settings saving upon destruction),
     * {@link com.github.czyzby.autumn.mvc.component.ui.SkinService} (skin disposing). */
    public static final int VERY_LOW_PRIORITY = -2;
    /** -3. Executes last. Used by: {@link com.github.czyzby.autumn.mvc.component.ui.InterfaceService} (first view
     * initiation and showing); {@link com.github.czyzby.autumn.mvc.component.asset.AssetService} (assets disposing),
     * {@link com.github.czyzby.autumn.mvc.component.preferences.PreferencesService} (saving preferences) */
    public static final int MIN_PRIORITY = -3;
}