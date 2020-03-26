package com.github.czyzby.lml.util;

import java.io.Writer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;
import com.github.czyzby.lml.parser.LmlData;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.github.czyzby.lml.parser.impl.tag.Dtd;

/** An {@link ApplicationListener} implementation that manages a list of {@link AbstractLmlView LML views}. Forces the
 * user to prepare a {@link LmlParser} with {@link #createParser()} method. Ensures smooth view transitions. Adds
 * default actions with {@link #addDefaultActions()} method: "exit" closes the application after smooth screen hiding,
 * "close" is a no-op utility method for dialogs and "setView" changes the current view according to the actor's ID (the
 * ID has to match name of a class extending {@link AbstractLmlView}). Most of its settings are customizable - go
 * through protected methods API for more info.
 *
 * <p>
 * {@link AbstractLmlView} instances managed by this class are required to properly implement
 * {@link AbstractLmlView#getTemplateFile()}. Note that the views are likely to be accessed with reflection, so make
 * sure to include their classes in GWT reflection mechanism.
 *
 * <p>
 * What LibGDX {@link com.badlogic.gdx.Game Game} is to {@link com.badlogic.gdx.Screen Screen}, this class is the same
 * thing to {@link AbstractLmlView}. Except it adds much more functionalities.
 *
 * @author MJ */
public abstract class LmlApplicationListener implements ApplicationListener {
    private final ObjectMap<Class<? extends AbstractLmlView>, AbstractLmlView> views = GdxMaps.newObjectMap();
    private final ObjectMap<String, Class<? extends AbstractLmlView>> aliases = GdxMaps.newObjectMap();
    private final ViewChangeRunnable viewChangeRunnable = new ViewChangeRunnable();
    private AbstractLmlView currentView;
    private LmlParser lmlParser;
    private boolean clearActorsMap;
    private boolean clearMetaData;

    /** @return {@link LmlParser} instance created with {@link #createParser()} method.
     * @see LmlParser */
    public LmlParser getParser() {
        return lmlParser;
    }

    /** @return currently displayed {@link AbstractLmlView}. Can be null. */
    public AbstractLmlView getCurrentView() {
        return currentView;
    }

    /** @param currentView will be immediately set as {@link #getCurrentView() current view}. Note that this is not a
     *            part of public API: to ensure smooth view transitions, use {@link #setView(AbstractLmlView)}
     *            method. */
    protected void setCurrentView(final AbstractLmlView currentView) {
        this.currentView = currentView;
    }

    /** This method is automatically invoked with {@link AbstractLmlView#getViewId()} (if it returns a non-empty value)
     * on each view initiated with {@link #initiateView(AbstractLmlView)}. This method can be invoked manually for lazy
     * views loading: they will be accessible through the chosen alias in LML templates even when their instance is not
     * created yet.
     *
     * <p>
     * If you initiate all views eagerly (in the correct order in which they reference one another), they are likely to
     * already registered by the time {@link LmlParser} parses their templates. However, calling this method for each of
     * your expected views in {@link #create()} is considered a good practice if you use {@code setView} LML method to
     * switch between screens.
     *
     * @param alias name of the view in LML templates. It will be used by {@code setView} LML method to choose the
     *            appropriate view class.
     * @param viewClass will be mapped to the selected alias. */
    protected void addClassAlias(final String alias, final Class<? extends AbstractLmlView> viewClass) {
        aliases.put(alias, viewClass);
    }

    /** @return direct reference to {@link AbstractLmlView} instances cache. Views (map values) are mapped by their
     *         classes (map keys). */
    protected ObjectMap<Class<? extends AbstractLmlView>, AbstractLmlView> getViews() {
        return views;
    }

    /** Uses current {@link LmlParser} to generate a DTD schema file with all supported tags, macros and attributes.
     * Should be used only during development: DTD allows to validate LML templates during creation (and add content
     * assist thanks to XML support in your IDE), but is not used in any way by the {@link LmlParser} in runtime.
     *
     * @param file path to the file where DTD schema should be saved. Advised to be local or absolute. Note that some
     *            platforms (GWT) do not support file saving - this method should be used on desktop platform and only
     *            during development.
     * @throws GdxRuntimeException when unable to save DTD schema.
     * @see Dtd */
    public void saveDtdSchema(final FileHandle file) {
        try {
            final Writer appendable = file.writer(false, "UTF-8");
            final boolean strict = lmlParser.isStrict();
            lmlParser.setStrict(false); // Temporary setting to non-strict to generate as much tags as possible.
            createDtdSchema(lmlParser, appendable);
            appendable.close();
            lmlParser.setStrict(strict);
        } catch (final Exception exception) {
            throw new GdxRuntimeException("Unable to save DTD schema.", exception);
        }
    }

    /** @param clearActorsMap if true, {@link LmlParser#getActorsMappedByIds() IDs to actors map} will be cleared after
     *            each view parsing. This prevents from injecting or modifying actors from previous views if ID
     *            collisions occur. When this value is set to true, you cannot access actors with their IDs using
     *            {@link LmlParser#getActorsMappedByIds()}. When set to false, you're advised to avoid IDs collisions in
     *            all views. Defaults to false.
     * @see LmlParser#getActorsMappedByIds()
     * @see com.github.czyzby.lml.annotation.LmlActor */
    protected void setClearActorsMap(final boolean clearActorsMap) {
        this.clearActorsMap = clearActorsMap;
    }

    /** @param clearMetaData if true, {@link LmlUtilities#clearLmlUserObjects(Iterable)} will be called with parsed
     *            every actor after view is created. While this limits the amount of objects assigned to each actor and
     *            kept at runtime, this option should be used with care, as some features rely on this mechanism. Use
     *            when absolutely sure that it doesn't break anything. Defaults to false.
     * @see LmlUtilities#clearLmlUserObject(Actor)
     * @see LmlUtilities#clearLmlUserObjects(Iterable) */
    protected void setClearMetaData(final boolean clearMetaData) {
        this.clearMetaData = clearMetaData;
    }

    /** This is a utility method that allows you to hook up into DTD generation process or even modify it completely.
     * This method is called by {@link #saveDtdSchema(FileHandle)} after the parser was already set to non-strict. By
     * default, this method calls standard DTD utility method: {@link Dtd#saveSchema(LmlParser, Appendable)}. By
     * overriding this method, you can generate minified schema with
     * {@link Dtd#saveMinifiedSchema(LmlParser, Appendable)} or manually append some customized tags and attributes
     * using {@link Appendable} API.
     *
     * <p>
     * If you want to generate DTD schema file for your LML parser, use {@link #saveDtdSchema(FileHandle)} method
     * instead.
     *
     * @param parser its schema will be generated.
     * @param appendable a reference to target file.
     * @see #saveDtdSchema(FileHandle)
     * @throws Exception if your saving method throws any exception, it will wrapped with {@link GdxRuntimeException}
     *             and rethrown. */
    protected void createDtdSchema(final LmlParser parser, final Appendable appendable) throws Exception {
        Dtd.saveSchema(parser, appendable);
    }

    /** Called when application is created.
     *
     * <p>
     * Prepares {@link LmlParser} with {@link #createParser()} method. Adds default actions present in all LML
     * templates. When overridden, make sure to call super. */
    @Override
    public void create() {
        lmlParser = createParser();
        addDefaultActions();
    }

    /** Called by {@link #create()} after creation of the {@link LmlParser} using {@link #createParser()} method.
     * Registers default actions available in all views. */
    protected void addDefaultActions() {
        final LmlData data = lmlParser.getData();
        // Closes the application after screen transition.
        data.addActorConsumer("exit", new ActorConsumer<Void, Object>() {
            @Override
            public Void consume(final Object actor) {
                GdxUtilities.clearInputProcessor();
                exit();
                return null;
            }
        });
        // Does nothing. Utility for dialogs: <dialog> ... <textButton onResult="close"> ...
        data.addActorConsumer("close", new ActorConsumer<Void, Object>() {
            @Override
            public Void consume(final Object actor) {
                return null;
            }
        });
        // Changes current view. Uses actor ID to determine view's class.
        data.addActorConsumer("setView", new ActorConsumer<Void, Actor>() {
            @Override
            public Void consume(final Actor actor) {
                final String viewClassName = LmlUtilities.getActorId(actor);
                final Class<? extends AbstractLmlView> viewClass = LmlApplicationListener.this
                        .getViewClass(viewClassName);
                setView(viewClass);
                return null;
            }
        });
    }

    /** @param viewClassName a qualified name of the view class (including package and class name) or a class alias
     *            registered with {@link #addClassAlias(String, Class)}.
     * @return the corresponding view class object.
     * @throws GdxRuntimeException if unable to determine view class. */
    @SuppressWarnings("unchecked")
    protected Class<? extends AbstractLmlView> getViewClass(final String viewClassName) {
        if (aliases.containsKey(viewClassName)) {
            return aliases.get(viewClassName);
        }
        try {
            return ClassReflection.forName(viewClassName);
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException(
                    "Unable to determine view class: " + viewClassName
                            + ". Does a class with such name exists? Was such class alias properly registered?",
                    exception);
        }
    }

    /** Smoothly hides the {@link #getCurrentView() current view} and closes the application.na */
    public void exit() {
        if (currentView == null) {
            GdxUtilities.exit();
        } else {
            currentView.getStage().addAction(Actions.sequence(getViewHidingAction(currentView),
                    Actions.run(GdxUtilities.getApplicationClosingRunnable())));
        }
    }

    /** @return a new customized instance of {@link LmlParser} used to process LML templates.
     * @see Lml
     * @see LmlParserBuilder */
    protected abstract LmlParser createParser();

    /** Calls {@link AbstractLmlView#resize(int, int, boolean)} on current view if it isn't empty.
     *
     * @param width current application width.
     * @param height current application height.
     * @see #isCenteringCameraOnResize() */
    @Override
    public void resize(final int width, final int height) {
        if (currentView != null) {
            currentView.resize(width, height, isCenteringCameraOnResize());
        }
    }

    /** @return if true, camera will be centered when resize event occurs. Defaults to false. When using certain
     *         viewports (like {@link com.badlogic.gdx.utils.viewport.ScreenViewport screen viewport}), this method
     *         should return true.
     * @see #resize(int, int) */
    protected boolean isCenteringCameraOnResize() {
        return false;
    }

    /** Clears the screen using {@link GdxUtilities#clearScreen()}. Calls {@link AbstractLmlView#render(float)} on
     * current view (if it isn't empty) with current delta time. */
    @Override
    public void render() {
        GdxUtilities.clearScreen();
        if (currentView != null) {
            currentView.render(Gdx.graphics.getDeltaTime());
        }
    }

    /** Calls {@link AbstractLmlView#pause()} on current view if it isn't empty. */
    @Override
    public void pause() {
        if (currentView != null) {
            currentView.pause();
        }
    }

    /** Calls {@link AbstractLmlView#resume()} on current view if it isn't empty. */
    @Override
    public void resume() {
        if (currentView != null) {
            currentView.resume();
        }
    }

    /** Calls {@link AbstractLmlView#dispose()} on each stored view. When overriding this method, make sure to call
     * super or dispose your views manually. */
    @Override
    public void dispose() {
        Disposables.disposeOf(views.values());
    }

    /** @param viewClass {@link AbstractLmlView} extension that represents a single view.
     * @return an instance of the view. If the instance is not currently cached, it will be created using default
     *         no-argument constructor with reflection.
     * @see #initiateView(AbstractLmlView) */
    protected AbstractLmlView getView(final Class<? extends AbstractLmlView> viewClass) {
        if (!views.containsKey(viewClass)) {
            // Cached version is not present - asking the parser to create and fill view:
            final AbstractLmlView view = getInstanceOf(viewClass);
            initiateView(view);
            return view;
        }
        return views.get(viewClass);
    }

    /** Disposes of {@link AbstractLmlView} instances. Clears views cache. Note that {@link #getCurrentView() current
     * view} will be set as null, so invoking this method is advised after the current view is already hidden. */
    public void clearViews() {
        Disposables.disposeOf(views.values());
        currentView = null;
        views.clear();
    }

    /** @param viewClass {@link AbstractLmlView} extension that represents a single view. If an instance of this view
     *            class is managed by the application listener, it will be {@link AbstractLmlView#dispose() disposed}
     *            and removed from views cache. If the view is currently displayed, {@link #getCurrentView() current
     *            view} will be set to null. */
    public void clearView(final Class<? extends AbstractLmlView> viewClass) {
        final AbstractLmlView view = views.get(viewClass);
        if (view != null) {
            view.dispose();
            views.remove(viewClass);
            validateCurrentView(view);
        }
    }

    /** @param removedView is being removed. If it is currently displayed, current view will be set to null. */
    private void validateCurrentView(final AbstractLmlView removedView) {
        if (removedView == currentView) {
            currentView = null;
        }
    }

    /** @param view {@link AbstractLmlView} extension that represents a single view. Will be
     *            {@link AbstractLmlView#dispose() disposed} and removed from views cache. If the view is currently
     *            displayed, {@link #getCurrentView() current view} will be set to null. */
    public void clearView(final AbstractLmlView view) {
        view.dispose();
        views.remove(view.getClass());
        validateCurrentView(view);
    }

    /** All currently cached views will be reloaded using {@link #reloadView(AbstractLmlView)} method. Note that this
     * method should be called when the current view is hidden, as parsing of multiple templates might cause some delays
     * (especially on slower devices). Useful for reloading localized texts after i18n bundle change. */
    public void reloadViews() {
        for (final AbstractLmlView view : views.values()) {
            reloadView(view);
        }
    }

    /** @param view will receive {@link AbstractLmlView#clear()} call. Its actors will be removed. Its template file
     *            accessed by {@link AbstractLmlView#getTemplateFile()} will be parsed by the {@link LmlParser} and used
     *            to fill the view. */
    public void reloadView(final AbstractLmlView view) {
        view.clear();
        view.getStage().getRoot().clearChildren();
        lmlParser.createView(view, view.getTemplateFile());
    }

    /** @param view its instance will be cached and returned each time it is requested with {@link #getView(Class)}
     *            method. Its {@link AbstractLmlView#getViewId()} will be used to create a class alias with
     *            {@link #addClassAlias(String, Class)}. Its template file accessed by
     *            {@link AbstractLmlView#getTemplateFile()} will be parsed by the {@link LmlParser} and used to fill the
     *            view.
     * @see #setClearActorsMap(boolean)
     * @see #setClearMetaData(boolean) */
    protected void initiateView(final AbstractLmlView view) {
        views.put(view.getClass(), view);
        final String viewId = view.getViewId();
        if (Strings.isNotEmpty(viewId)) {
            addClassAlias(viewId, view.getClass());
        }
        lmlParser.createView(view, view.getTemplateFile());
        if (clearActorsMap) {
            lmlParser.getActorsMappedByIds().clear();
        }
        if (clearMetaData) {
            LmlUtilities.clearLmlUserObjects(view.getStage().getActors());
        }
    }

    /** @param viewClass {@link AbstractLmlView} extension that represents a single view. Its instance is requested.
     * @return a new instance of the passed class. By default, the instance is created using the default no-argument
     *         constructor using reflection. Override this method to change the view creation way. */
    protected AbstractLmlView getInstanceOf(final Class<? extends AbstractLmlView> viewClass) {
        return Reflection.newInstance(viewClass);
    }

    /** @param viewClass {@link AbstractLmlView} extension that represents a single view. An instance of this class will
     *            become the current view after view transition.
     * @see #setView(AbstractLmlView) */
    public void setView(final Class<? extends AbstractLmlView> viewClass) {
        setView(getView(viewClass), null);
    }

    /** @param viewClass {@link AbstractLmlView} extension that represents a single view. An instance of this class will
     *            become the current view after view transition.
     * @param doAfterHide will be executed after the current view is fully hidden. Is never executed if there was no
     *            current view.
     * @see #setView(AbstractLmlView, Action) */
    public void setView(final Class<? extends AbstractLmlView> viewClass, final Action doAfterHide) {
        setView(getView(viewClass), doAfterHide);
    }

    /** @param view will be set as the current view after view transition. Current screen (if any exists) will receive a
     *            {@link AbstractLmlView#hide()} call. The new screen will be resized using
     *            {@link AbstractLmlView#resize(int, int, boolean)} and then will receive a
     *            {@link AbstractLmlView#show()} call.
     * @see #getViewShowingAction(AbstractLmlView)
     * @see #getViewHidingAction(AbstractLmlView) */
    public void setView(final AbstractLmlView view) {
        setView(view, null);
    }

    /** @param view will be set as the current view after view transition. Current screen (if any exists) will receive a
     *            {@link AbstractLmlView#hide()} call. The new screen will be resized using
     *            {@link AbstractLmlView#resize(int, int, boolean)} and then will receive a
     *            {@link AbstractLmlView#show()} call.
     * @param doAfterHide will be executed after the current view is fully hidden. Is never executed if there was no
     *            current view.
     * @see #getViewShowingAction(AbstractLmlView)
     * @see #getViewHidingAction(AbstractLmlView) */
    public void setView(final AbstractLmlView view, final Action doAfterHide) {
        if (currentView != null) {
            viewChangeRunnable.setView(view);
            Gdx.input.setInputProcessor(null);
            currentView.hide();
            final Action hideAction = doAfterHide == null
                    ? Actions.sequence(getViewHidingAction(currentView), Actions.run(viewChangeRunnable))
                    : Actions.sequence(getViewHidingAction(currentView), doAfterHide, Actions.run(viewChangeRunnable));
            currentView.getStage().addAction(hideAction);
        } else {
            currentView = view;
            currentView.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), isCenteringCameraOnResize());
            Gdx.input.setInputProcessor(currentView.getStage());
            currentView.show();
            currentView.getStage().addAction(getViewShowingAction(view));
        }
    }

    /** @param view is about to be hidden.
     * @return {@link Action} instance used to hide the view. A simple fade-out action by default.
     * @see #getViewTransitionDuration() */
    protected Action getViewHidingAction(final AbstractLmlView view) {
        return Actions.fadeOut(getViewTransitionDuration(), Interpolation.fade);
    }

    /** @param view is about to be shown.
     * @return {@link Action} instance used to show the view. By default, makes sure that the view is transparent and
     *         begins a simple fade-in action.
     * @see #getViewTransitionDuration() */
    protected Action getViewShowingAction(final AbstractLmlView view) {
        return Actions.sequence(Actions.alpha(0f), Actions.fadeIn(getViewTransitionDuration(), Interpolation.fade));
    }

    /** @return length of a single view hiding or showing action used by default view transition actions. In seconds.
     * @see #getViewShowingAction(AbstractLmlView)
     * @see #getViewHidingAction(AbstractLmlView) */
    protected float getViewTransitionDuration() {
        return 0.4f;
    }

    /** {@link Action} utility. Used to change the current view thanks to {@link Actions#run(Runnable)}.
     *
     * @author MJ */
    protected class ViewChangeRunnable implements Runnable {
        private AbstractLmlView view;

        /** @param view should be shown. */
        public void setView(final AbstractLmlView view) {
            this.view = view;
        }

        @Override
        public void run() {
            currentView = null;
            LmlApplicationListener.this.setView(view, null);
        }
    }
}