package com.github.czyzby.autumn.mvc.component.ui.action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewController;

/** Contains runnables commonly used in Scene2D actions. Mostly for internal use: static methods often reuse object and
 * do not assume concurrent use. Make new instances of nested classes if you want to use these utilities.
 *
 * @author MJ */
public class CommonActionRunnables {
    private static ActionPosterRunnable POST_ACTION_RUNNABLE = new ActionPosterRunnable();
    private static InputSetterRunnable SET_INPUT_RUNNABLE = new InputSetterRunnable();
    private static ViewShowerRunnable SHOW_VIEW_RUNNABLE = new ViewShowerRunnable();
    private static CurrentThemeClearerRunnable CLEAR_THEME_RUNNABLE = new CurrentThemeClearerRunnable();
    private static CurrentThemeSetterRunnable SET_THEME_RUNNABLE = new CurrentThemeSetterRunnable();
    private static Runnable EXIT_APPLICATION_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            Gdx.app.exit();
        }
    };
    private static Runnable CLEAR_INPUT_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            Gdx.input.setInputProcessor(null);
        }
    };

    private CommonActionRunnables() {
    }

    /** @param inputProcessor the next input processor.
     * @return will set application's input processor when run. */
    public static Runnable getInputSetterRunnable(final InputProcessor inputProcessor) {
        return SET_INPUT_RUNNABLE.setProcessor(inputProcessor);
    }

    /** @return will clear input processor when run. */
    public static Runnable getInputClearerRunnable() {
        return CLEAR_INPUT_RUNNABLE;
    }

    /** @param interfaceService manages views.
     * @param controllerToShow the next view.
     * @return will schedule showing of the passed view when run. */
    public static Runnable getViewSetterRunnable(final InterfaceService interfaceService,
            final ViewController controllerToShow) {
        return SHOW_VIEW_RUNNABLE.setData(interfaceService, controllerToShow);
    }

    /** @param actionToPost will be scheduled to be executed on the main thread.
     * @return schedules passed action when run. */
    public static Runnable getActionPosterRunnable(final Runnable actionToPost) {
        return POST_ACTION_RUNNABLE.setActionToPost(actionToPost);
    }

    /** @return runnable that closes the application on invocation. */
    public static Runnable getApplicationClosingRunnable() {
        return EXIT_APPLICATION_RUNNABLE;
    }

    /** @param musicService contains current theme.
     * @return will clear current theme on invocation. */
    public static Runnable getMusicThemeClearerRunnable(final MusicService musicService) {
        return CLEAR_THEME_RUNNABLE.setMusicService(musicService);
    }

    /** @param musicService will contain theme.
     * @param themeToSet will be set as current theme.
     * @return will set current theme on invocation. */
    public static Runnable getMusicThemeSetterRunnable(final MusicService musicService, final Music themeToSet) {
        return SET_THEME_RUNNABLE.setData(musicService, themeToSet);
    }

    /** Sets application's input processor.
     *
     * @author MJ */
    public static class InputSetterRunnable implements Runnable {
        private InputProcessor processor;

        @Override
        public void run() {
            Gdx.input.setInputProcessor(processor);
            processor = null;
        }

        public InputSetterRunnable setProcessor(final InputProcessor processor) {
            this.processor = processor;
            return this;
        }
    }

    /** Schedules showing of a chosen view.
     *
     * @author MJ */
    public static class ViewShowerRunnable implements Runnable {
        private InterfaceService interfaceService;
        private ViewController controllerToShow;

        @Override
        public void run() {
            interfaceService.show(controllerToShow);
            controllerToShow = null;
            interfaceService = null;
        }

        public ViewShowerRunnable setData(final InterfaceService interfaceService,
                final ViewController controllerToShow) {
            this.interfaceService = interfaceService;
            this.controllerToShow = controllerToShow;
            return this;
        }
    }

    /** Schedules passed action to the main thread.
     *
     * @author MJ */
    public static class ActionPosterRunnable implements Runnable {
        private Runnable actionToPost;

        @Override
        public void run() {
            Gdx.app.postRunnable(actionToPost);
            actionToPost = null;
        }

        public ActionPosterRunnable setActionToPost(final Runnable actionToPost) {
            this.actionToPost = actionToPost;
            return this;
        }
    }

    /** Clears current theme of a music service.
     *
     * @author MJ */
    public static class CurrentThemeClearerRunnable implements Runnable {
        private MusicService musicService;

        public CurrentThemeClearerRunnable setMusicService(final MusicService musicService) {
            this.musicService = musicService;
            return this;
        }

        @Override
        public void run() {
            musicService.clearCurrentTheme();
            musicService = null;
        }
    }

    /** Clears current theme of a music service.
     *
     * @author MJ */
    public static class CurrentThemeSetterRunnable implements Runnable {
        private MusicService musicService;
        private Music currentTheme;

        public CurrentThemeSetterRunnable setData(final MusicService musicService, final Music currentTheme) {
            this.musicService = musicService;
            this.currentTheme = currentTheme;
            return this;
        }

        @Override
        public void run() {
            musicService.playCurrentTheme(currentTheme, false);
            musicService = null;
            currentTheme = null;
        }
    }
}