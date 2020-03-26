package com.github.czyzby.autumn.mvc.config;

import com.badlogic.gdx.assets.AssetLoaderParameters;

/** Contains all messages posted by Autumn MVC components using a
 * {@link com.github.czyzby.autumn.processor.event.MessageDispatcher}.
 *
 * @author MJ */
public class AutumnMessage {
    /** Can be extended to contain all application's messages, but should not be initiated. */
    protected AutumnMessage() {
    }

    /** Posted each time SOME assets scheduled with {@link com.github.czyzby.autumn.mvc.component.asset.AssetService}
     * are loaded. This is not posted if assets where loaded on demand with
     * {@link com.github.czyzby.autumn.mvc.component.asset.AssetService#finishLoading(String, Class)} or
     * {@link com.github.czyzby.autumn.mvc.component.asset.AssetService#finishLoading(String, Class, AssetLoaderParameters)}
     * methods. */
    public static final String ASSETS_LOADED = "AMVC_assetsLoaded";

    /** Posted when all application's {@link com.badlogic.gdx.scenes.scene2d.ui.Skin}s are fully loaded. */
    public static final String SKINS_LOADED = "AMVC_skinsLoaded";

    /** Posted when the game's window is resized. Posted AFTER
     * {@link com.github.czyzby.autumn.mvc.component.ui.InterfaceService} resizes current view (if any is present). */
    public static final String GAME_RESIZED = "AMVC_gameResized";

    /** Posted when the game is paused. Posted AFTER {@link com.github.czyzby.autumn.mvc.component.ui.InterfaceService}
     * pauses current view (if any is present). */
    public static final String GAME_PAUSED = "AMVC_gamePaused";

    /** Posted when the game is resumed. Posted AFTER {@link com.github.czyzby.autumn.mvc.component.ui.InterfaceService}
     * resumes current view (if any is present). */
    public static final String GAME_RESUMED = "AMVC_gameResumed";
}