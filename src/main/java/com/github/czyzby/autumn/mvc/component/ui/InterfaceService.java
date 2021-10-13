package com.github.czyzby.autumn.mvc.component.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;
import com.github.czyzby.autumn.mvc.component.i18n.LocaleService;
import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.autumn.mvc.component.ui.action.*;
import com.github.czyzby.autumn.mvc.component.ui.controller.*;
import com.github.czyzby.autumn.mvc.component.ui.controller.impl.AnnotatedViewDialogController;
import com.github.czyzby.autumn.mvc.component.ui.controller.impl.StandardViewRenderer;
import com.github.czyzby.autumn.mvc.component.ui.controller.impl.StandardViewResizer;
import com.github.czyzby.autumn.mvc.component.ui.controller.impl.StandardViewShower;
import com.github.czyzby.autumn.mvc.component.ui.dto.provider.ViewActionProvider;
import com.github.czyzby.autumn.mvc.component.ui.processor.ViewActionContainerAnnotationProcessor;
import com.github.czyzby.autumn.mvc.config.AutumnActionPriority;
import com.github.czyzby.autumn.mvc.config.AutumnMessage;
import com.github.czyzby.autumn.processor.event.MessageDispatcher;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.kiwi.util.gdx.preference.ApplicationPreferences;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.util.Lml;
import gdx.liftoff.config.Configuration;

import java.util.Locale;

/** Manages view controllers and a LML parser.
 *
 * @author MJ */
public class InterfaceService {
    /** Defines default resizing behavior. Can be modified statically before context initiation to set default behavior
     * for controllers that do not implement this interface. */
    public static ViewResizer DEFAULT_VIEW_RESIZER = new StandardViewResizer();
    /** Defines default showing and hiding behavior. Can be modified statically before context initiation to set default
     * behavior for controllers that do not implement this interface. */
    public static ViewShower DEFAULT_VIEW_SHOWER = new StandardViewShower();
    /** Defines default rendering behavior. Can be modified statically before context initiation to set default behavior
     * for controllers that do not implement this interface. */
    public static ViewRenderer DEFAULT_VIEW_RENDERER = new StandardViewRenderer();
    /** Defines default pausing and resuming behavior. Can be modified statically before context initiation to set
     * default behavior for controllers that do not implement this interface. Defaults to null. */
    public static ViewPauser DEFAULT_VIEW_PAUSER = null;
    /** Defines default initializing and destroying behavior. Can be modified statically before context initiation to
     * set default behavior for controllers that do not implement this interface. Defaults to null. */
    public static ViewInitializer DEFAULT_VIEW_INITIALIZER = null;

    /** Defaults prefix used to add dialog transition actions to LML parser upon dialog controllers' registrations. Can
     * be modified statically before context initiation to use another prefix. */
    public static String DIALOG_SHOWING_ACTION_PREFIX = "show:";
    /** Default prefix used to add screen transition actions to the LML parser upon controllers' registrations. Can be
     * modified statically before context initiation to use another prefix. */
    public static String SCREEN_TRANSITION_ACTION_PREFIX = "goto:";
    /** Length of views' fading in and out. Can be modified statically to change screen transition length, without
     * having to set a different action provider with {@link #setHidingActionProvider(ActionProvider)} or
     * {@link #setShowingActionProvider(ActionProvider)}. Retrieved each time a screen is shown or hidden using default
     * actions. */
    public static float DEFAULT_FADING_TIME = 0.25f;
    /** Upon initiation and each time locale is changed, it is parsed to string (separating values with -) and assigned
     * as a LML view argument which can be referenced with ${thisVariableValue} in templates. Changing this variable
     * (ideally - before building first screen) allows to choose argument's name. */
    private static String CURRENT_LOCALE = "currentLocale";

    private final ObjectMap<Class<?>, ViewController> controllers = GdxMaps.newObjectMap();
    private final ObjectMap<Class<?>, ViewDialogController> dialogControllers = GdxMaps.newObjectMap();
    private final ObjectMap<String, FileHandle> i18nBundleFiles = GdxMaps.newObjectMap();

    private final LmlParser parser = Lml.parser().build();
    private final Batch batch = new SpriteBatch();

    private final ScreenSwitchingRunnable screenSwitchingRunnable = new ScreenSwitchingRunnable(this);
    private ViewController currentController;
    private Locale lastLocale;
    private boolean isControllerHiding;

    private Runnable actionOnReload;
    private Runnable actionOnShow;
    private Runnable actionOnBundlesReload;

    @Inject private AssetService assetService;
    @Inject private LocaleService localeService;
    @Inject private MusicService musicService;
    @Inject private MessageDispatcher messageDispatcher;
    @Inject private ViewActionContainerAnnotationProcessor viewActionProcessor;

    private ActionProvider showingActionProvider = getDefaultViewShowingActionProvider();
    private ActionProvider hidingActionProvider = getDefaultViewHidingActionProvider();
    private ObjectProvider<Viewport> viewportProvider = getDefaultViewportProvider();

    /** Registers {@link com.badlogic.gdx.Preferences} object to the LML parser.
     *
     * @param preferencesKey key of the preferences as it appears in LML views.
     * @param preferencesPath path to the preferences. */
    public void addPreferencesToParser(final String preferencesKey, final String preferencesPath) {
        parser.getData().addPreferences(preferencesKey, ApplicationPreferences.getPreferences(preferencesPath));
    }

    /** Registers an action container globally for all views.
     *
     * @param actionContainerId ID of the action container as it appears in the views.
     * @param actionContainer contains view actions. */
    public void addViewActionContainer(final String actionContainerId, final ActionContainer actionContainer) {
        parser.getData().addActionContainer(actionContainerId, actionContainer);
    }

    /** Registers an action globally for all views.
     *
     * @param actionId ID of the action.
     * @param action will be available in views with the selected ID. */
    public void addViewAction(final String actionId, final ActorConsumer<?, ?> action) {
        parser.getData().addActorConsumer(actionId, action);
    }

    /** @param bundleId ID of the bundle as it appears in LML templates.
     * @param file file handle of the bundle. Will be read when bundles are requested (before view parsing, after locale
     *            change). */
    public void addBundleFile(final String bundleId, final FileHandle file) {
        i18nBundleFiles.put(bundleId, file);
    }

    @Initiate(priority = AutumnActionPriority.VERY_HIGH_PRIORITY)
    private void assignViewResources() {
        addDefaultViewActions();
    }

    private void saveLastLocale(final Locale currentLocale) {
        lastLocale = currentLocale;
        parser.getData().addArgument(CURRENT_LOCALE, LocaleService.fromLocale(lastLocale));
    }

    private void addDefaultViewActions() {
        parser.getData().addActorConsumer(ApplicationExitAction.ID, new ApplicationExitAction(this));
        parser.getData().addActorConsumer(ApplicationPauseAction.ID, new ApplicationPauseAction());
        parser.getData().addActorConsumer(ApplicationResumeAction.ID, new ApplicationResumeAction());
    }

    /** Allows to manually register a managed controller. For internal use mostly.
     *
     * @param mappedControllerClass class with which the controller is accessible. This does not have to be controller's
     *            actual class.
     * @param controller controller implementation, managing a single view. */
    public void registerController(final Class<?> mappedControllerClass, final ViewController controller) {
        controllers.put(mappedControllerClass, controller);
        if (Strings.isNotEmpty(controller.getViewId())) {
            parser.getData().addActorConsumer(SCREEN_TRANSITION_ACTION_PREFIX + controller.getViewId(),
                    new ScreenTransitionAction(this, mappedControllerClass));
        }
    }

    public void registerDialogController(final Class<?> mappedDialogControllerClass,
            final ViewDialogController dialogController) {
        dialogControllers.put(mappedDialogControllerClass, dialogController);
        if (Strings.isNotEmpty(dialogController.getId())) {
            parser.getData().addActorConsumer(DIALOG_SHOWING_ACTION_PREFIX + dialogController.getId(),
                    new DialogShowingAction(this, mappedDialogControllerClass));
        }
    }

    @Initiate(priority = AutumnActionPriority.MIN_PRIORITY)
    private void initiateFirstScreen() {
        for (final ViewController controller : controllers.values()) {
            if (controller.isFirst()) {
                show(controller);
                return;
            }
        }
        throw new GdxRuntimeException("At least one view has to be set as first.");
    }

    private void initiateView(final ViewController controller) {
        if (!controller.isCreated()) {
            validateLocale();
            // Action on locale change might initiate all views, which is pretty common if the user wants to pre-load
            // all views and dialogs. So we're double-checking if the view is really not created yet:
            if (controller.isCreated()) {
                return;
            }
            final ActionContainer actionContainer = controller.getActionContainer();
            registerViewSpecificActions(controller.getViewId(), actionContainer);
            controller.createView(this);
            unregisterViewSpecificActions(controller.getViewId(), actionContainer);
            parser.getActorsMappedByIds().clear();
        }
    }

    private void registerViewSpecificActions(final String controllerId, final ActionContainer actionContainer) {
        if (actionContainer != null) {
            parser.getData().addActionContainer(controllerId, actionContainer);
        }
        final Array<ViewActionProvider> viewSpecificActions = viewActionProcessor.getActionProviders();
        if (GdxArrays.isNotEmpty(viewSpecificActions)) {
            for (final ViewActionProvider actionProvider : viewSpecificActions) {
                actionProvider.register(parser, controllerId);
            }
        }
    }

    private void unregisterViewSpecificActions(final String controllerId, final ActionContainer actionContainer) {
        if (actionContainer != null) {
            parser.getData().removeActionContainer(controllerId);
        }
        final Array<ViewActionProvider> viewSpecificActions = viewActionProcessor.getActionProviders();
        if (GdxArrays.isNotEmpty(viewSpecificActions)) {
            for (final ViewActionProvider actionProvider : viewSpecificActions) {
                actionProvider.unregister(parser, controllerId);
            }
        }
    }

    private void validateLocale() {
        final Locale currentLocale = localeService.getCurrentLocale();
        if (!currentLocale.equals(lastLocale)) {
            saveLastLocale(currentLocale);
            for (final Entry<String, FileHandle> bundleData : i18nBundleFiles) {
                parser.getData().addI18nBundle(bundleData.key,
                        I18NBundle.createBundle(bundleData.value, currentLocale));
            }
            executeActionOnBundlesReload();
        }
    }

    /** @param actionOnBundlesReload will be executed each time i18n bundles are reloaded. */
    public void setActionOnBundlesReload(final Runnable actionOnBundlesReload) {
        this.actionOnBundlesReload = actionOnBundlesReload;
    }

    /** @return LML parser that should be used to construct views. */
    public LmlParser getParser() {
        return parser;
    }

    /** @return {@link SpriteBatch} instance used to render all views. */
    public Batch getBatch() {
        return batch;
    }

    /** @return default {@link Skin} used to build views. */
    public Skin getSkin() {
        return parser.getData().getDefaultSkin();
    }

    /** @param id ID of the requested skin. By default, case is ignored.
     * @return {@link Skin} with the selected ID. */
    public Skin getSkin(final String id) {
        return parser.getData().getSkin(id);
    }

    /** Hides current view (if present) and shows the view managed by the passed controller.
     *
     * @param controller class of the controller managing the view. */
    public void show(final Class<?> controller) {
        show(controllers.get(controller));
    }

    /** Hides current view (if present) and shows the view managed by the passed controller.
     *
     * @param controller class of the controller managing the view.
     * @param actionOnShow will be executed after the current screen is hidden. */
    public void show(final Class<?> controller, final Runnable actionOnShow) {
        this.actionOnShow = actionOnShow;
        show(controller);
    }

    /** Hides current view (if present) and shows the view managed by the chosen controller
     *
     * @param viewController will be set as the current view and shown. */
    public void show(final ViewController viewController) {
        if (currentController != null) {
            if (isControllerHiding) {
                switchToView(viewController);
            } else {
                hideCurrentViewAndSchedule(viewController);
            }
        } else {
            switchToView(viewController);
        }
    }

    /** Allows to show a globally registered dialog.
     *
     * @param dialogControllerClass class managing a single dialog. */
    public void showDialog(final Class<?> dialogControllerClass) {
        if (currentController != null) {
            dialogControllers.get(dialogControllerClass).show(currentController.getStage());
        }
    }

    private void switchToView(final ViewController viewController) {
        Gdx.app.postRunnable(screenSwitchingRunnable.switchToView(viewController));
    }

    private void hideCurrentViewAndSchedule(final ViewController viewController) {
        isControllerHiding = true;
        currentController.hide(Actions.sequence(hidingActionProvider.provideAction(currentController, viewController),
                Actions.run(CommonActionRunnables.getViewSetterRunnable(this, viewController))));
    }

    /** Forces eager initiation of all views managed by registered controllers. Initiates dialogs that cache and reuse
     * their dialog actor instance. */
    public void initiateAllControllers() {
        for (final ViewController controller : controllers.values()) {
            initiateView(controller);
        }
        for (final ViewDialogController controller : dialogControllers.values()) {
            if (controller instanceof AnnotatedViewDialogController) {
                final AnnotatedViewDialogController dialogController = (AnnotatedViewDialogController) controller;
                if (!dialogController.isInitiated() && dialogController.isCachingInstance()) {
                    dialogController.prepareDialogInstance();
                }
            }
        }
    }

    /** @return provider of viewports that should be used to construct stages. */
    public ObjectProvider<Viewport> getViewportProvider() {
        return viewportProvider;
    }

    /** @param viewportProvider used to construct stages. */
    public void setViewportProvider(final ObjectProvider<Viewport> viewportProvider) {
        this.viewportProvider = viewportProvider;
    }

    /** @param hidingActionProvider used to provide default actions that hide the views. Connected view of the provider
     *            will be the next view shown after this one and might be null. */
    public void setHidingActionProvider(final ActionProvider hidingActionProvider) {
        this.hidingActionProvider = hidingActionProvider;
    }

    /** @return provider used to retrieve default actions that hide the views. */
    public ActionProvider getHidingActionProvider() {
        return hidingActionProvider;
    }

    /** @param showingActionProvider used to provide default actions that show views. Should set input processor.
     *            Connected view of the provider will be the previous view shown before this one and might be null. */
    public void setShowingActionProvider(final ActionProvider showingActionProvider) {
        this.showingActionProvider = showingActionProvider;
    }

    /** @return provider used to retrieve default actions that show the views. */
    public ActionProvider getShowingActionProvider() {
        return showingActionProvider;
    }

    /** Hides current view, destroys all screens and shows the recreated current view. Note that it won't recreate all
     * views that were previously initiated, as views are constructed on demand.
     *
     * @see #initiateAllControllers() */
    public void reload() {
        currentController
                .hide(Actions.sequence(hidingActionProvider.provideAction(currentController, currentController),
                        Actions.run(CommonActionRunnables.getActionPosterRunnable(getViewReloadingRunnable()))));
    }

    /** Hides current view, destroys all screens and shows the recreated current view. Note that it won't recreate all
     * views that were previously initiated, as views are constructed on demand.
     *
     * @param actionOnReload will be executed after the current screen is hidden.
     * @see #initiateAllControllers() */
    public void reload(final Runnable actionOnReload) {
        this.actionOnReload = actionOnReload;
        reload();
    }

    /** Forces destruction of the selected view. The view should not be currently shown, as it still might get a render
     * call if next screen was not set.
     *
     * @param viewController will be destroyed.
     * @see #remove(Class) */
    public void destroy(final Class<?> viewController) {
        controllers.get(viewController).destroyView();
    }

    /** Forces destruction of the selected dialog (if an instance is actually kept). Proper UI behavior is ensured only
     * if dialog is not currently shown on the stage.
     *
     * @param dialogController its dialog will be destroyed.
     * @see #removeDialog(Class) */
    public void destroyDialog(final Class<?> dialogController) {
        dialogControllers.get(dialogController).destroyDialog();
    }

    /** Forces destruction and complete removal of the controller from the service. The view should not be currently
     * shown, as it still might get a render call if next screen was not set.
     *
     * @param viewController will be destroyed and removed. Will no longer be available.
     * @see #destroy(Class) */
    public void remove(final Class<?> viewController) {
        destroy(viewController);
        controllers.remove(viewController);
    }

    /** Forces destruction and complete removal of the dialog controller from the service. Proper UI behavior is ensured
     * only if dialog is not currently shown on the stage.
     *
     * @param dialogController will be destroyed and removed. Will no longer be available.
     * @see #destroyDialog(Class) */
    public void removeDialog(final Class<?> dialogController) {
        destroyDialog(dialogController);
        dialogControllers.remove(dialogController);
    }

    private Runnable getViewReloadingRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                executeActionOnReload();
                reloadViews();
            }
        };
    }

    private void executeActionOnBundlesReload() {
        if (actionOnBundlesReload != null) {
            actionOnBundlesReload.run();
        }
    }

    private void executeActionOnReload() {
        if (actionOnReload != null) {
            actionOnReload.run();
            actionOnReload = null;
        }
    }

    private void executeActionOnShow() {
        if (actionOnShow != null) {
            actionOnShow.run();
            actionOnShow = null;
        }
    }

    private void reloadViews() {
        destroyViews();
        destroyDialogs();
        final ViewController viewToShow = currentController;
        currentController = null;
        show(viewToShow);
    }

    /** Renders the current view, if present.
     *
     * @param delta time passed since the last update. */
    public void render(final float delta) {
        if (currentController != null) {
            currentController.render(delta);
        }
    }

    /** Resizes the current view, if present.
     *
     * @param width new width of the screen.
     * @param height new height of the screen. */
    public void resize(final int width, final int height) {
        if (currentController != null) {
            currentController.resize(width, height);
        }
        messageDispatcher.postMessage(AutumnMessage.GAME_RESIZED);
    }

    /** Pauses the current view, if present. */
    public void pause() {
        if (currentController != null) {
            currentController.pause();
        }
        messageDispatcher.postMessage(AutumnMessage.GAME_PAUSED);
    }

    /** Resumes the current view, if present. */
    public void resume() {
        if (currentController != null) {
            currentController.resume();
        }
        messageDispatcher.postMessage(AutumnMessage.GAME_RESUMED);
    }

    /** @return controller of currently shown view. Might be null. Mostly for internal use. */
    public ViewController getCurrentController() {
        return currentController;
    }

    /** @param forClass class associated with the controller. Does not have to be a
     *            {@link ViewController} - can be a wrapped by an
     *            auto-generated controller instance.
     * @return instance of the passed class or a controller wrapping the selected class. Can be null. */
    public ViewController getController(final Class<?> forClass) {
        return controllers.get(forClass);
    }

    /** @param forClass class associated with the dialog controller. Does not have to be a
     *            {@link ViewDialogController} - can be a wrapped
     *            by an auto-generated controller instance.
     * @return instance of the passed class or a dialog controller wrapping the selected class. Can be null. */
    public ViewDialogController getDialogController(final Class<?> forClass) {
        return dialogControllers.get(forClass);
    }

    @Destroy(priority = AutumnActionPriority.LOW_PRIORITY)
    private void dispose() {
        destroyViews();
        destroyDialogs();
        controllers.clear();
        batch.dispose();
    }

    private void destroyViews() {
        for (final ViewController controller : controllers.values()) {
            controller.destroyView();
        }
    }

    private void destroyDialogs() {
        for (final ViewDialogController dialogController : dialogControllers.values()) {
            dialogController.destroyDialog();
        }
    }

    /** Allows to smoothly close the application by hiding the current screen and calling
     * {@link com.badlogic.gdx.Application#exit()}. */
    public void exitApplication() {
        if (currentController != null) {
            currentController.hide(Actions.sequence(hidingActionProvider.provideAction(currentController, null),
                    Actions.run(CommonActionRunnables.getApplicationClosingRunnable())));
        } else {
            Gdx.app.exit();
        }
    }

    /** @return an array containing all managed controllers. Note that this is not used by the service internally and
     *         can be safely modified. */
    public Array<ViewController> getControllers() {
        return GdxArrays.newArray(controllers.values());
    }

    /** @return an array containing all managed dialog controllers. Note that this is not used by the service internally
     *         and can be safely modified. */
    public Array<ViewDialogController> getDialogControllers() {
        return GdxArrays.newArray(dialogControllers.values());
    }

    private ActionProvider getDefaultViewShowingActionProvider() {
        return new ActionProvider() {
            @Override
            public Action provideAction(final ViewController forController, final ViewController previousController) {
                if (musicService.getCurrentTheme() == null && GdxArrays.isNotEmpty(forController.getThemes())) {
                    final Music currentTheme = forController.getThemes().random();
                    return Actions.sequence(Actions.alpha(0f), Actions.fadeIn(DEFAULT_FADING_TIME),
                            Actions.run(CommonActionRunnables.getMusicThemeSetterRunnable(musicService, currentTheme)),
                            Actions.run(CommonActionRunnables.getInputSetterRunnable(forController.getStage())),
                            MusicFadingAction.fadeIn(currentTheme, MusicService.DEFAULT_THEME_FADING_TIME,
                                    musicService.getMusicVolume()));
                }
                return Actions.sequence(Actions.alpha(0f), Actions.fadeIn(DEFAULT_FADING_TIME),
                        Actions.run(CommonActionRunnables.getInputSetterRunnable(forController.getStage())));
            }
        };
    }

    private ActionProvider getDefaultViewHidingActionProvider() {
        return new ActionProvider() {
            @Override
            public Action provideAction(final ViewController forController, final ViewController nextController) {
                final Music currentTheme = musicService.getCurrentTheme();
                if (currentTheme == null || isThemeAvailableInNextView(nextController, currentTheme)) {
                    return Actions.sequence(Actions.run(CommonActionRunnables.getInputClearerRunnable()),
                            Actions.fadeOut(DEFAULT_FADING_TIME));
                }
                return Actions.sequence(Actions.run(CommonActionRunnables.getInputClearerRunnable()), Actions.parallel(
                        MusicFadingAction.fadeOut(currentTheme, MusicService.DEFAULT_THEME_FADING_TIME),
                        Actions.sequence(Actions.delay(MusicService.DEFAULT_THEME_FADING_TIME - DEFAULT_FADING_TIME),
                                Actions.fadeOut(DEFAULT_FADING_TIME))),
                        Actions.run(CommonActionRunnables.getMusicThemeClearerRunnable(musicService)));
            }

            private boolean isThemeAvailableInNextView(final ViewController nextController, final Music currentTheme) {
                return nextController != null && currentTheme != null
                        && GdxArrays.isNotEmpty(nextController.getThemes())
                        && nextController.getThemes().contains(currentTheme, false);
            }
        };
    }

    private static ObjectProvider<Viewport> getDefaultViewportProvider() {
        return new ObjectProvider<Viewport>() {
            @Override
            public Viewport provide() {
                return new FitViewport(Configuration.WIDTH, Configuration.HEIGHT);
            }
        };
    }

    /** Avoids anonymous classes.
     *
     * @author MJ */
    private static class ScreenSwitchingRunnable implements Runnable {
        private final InterfaceService interfaceService;
        private ViewController controllerToShow;

        public ScreenSwitchingRunnable(final InterfaceService interfaceService) {
            this.interfaceService = interfaceService;
        }

        public Runnable switchToView(final ViewController controllerToShow) {
            interfaceService.executeActionOnShow();
            this.controllerToShow = controllerToShow;
            return this;
        }

        @Override
        public void run() {
            interfaceService.isControllerHiding = false;
            final ViewController previousController = interfaceService.currentController;
            interfaceService.currentController = controllerToShow;
            interfaceService.initiateView(controllerToShow);
            controllerToShow.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            interfaceService.currentController.show(interfaceService.showingActionProvider
                    .provideAction(interfaceService.currentController, previousController));
            controllerToShow = null;
        }
    }
}
