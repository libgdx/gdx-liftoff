package com.github.czyzby.autumn.mvc.component.ui.controller.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewController;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewInitializer;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewPauser;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewResizer;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewShower;
import com.github.czyzby.autumn.mvc.component.ui.dto.ThemesAssignmentAction;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.util.LmlUtilities;

/** Default controller wrapper for an object that isn't an implementation of
 * {@link ViewController}, but is annotated with
 * {@link View}. Generally should not be used manually.
 *
 * @author MJ */
public class AnnotatedViewController extends AbstractAnnotatedController implements ViewController {
    private final Array<Music> themes = GdxArrays.newArray();
    private final View viewData;
    private final String id;

    private Stage stage;
    private int currentThemeIndex;

    private ViewRenderer viewRenderer;
    private ViewResizer viewResizer;
    private ViewPauser viewPauser;
    private ViewShower viewShower;
    private ViewInitializer viewInitializer;
    // Lazy initiation to allow setting default renderers etc. in @Initiate methods.
    private boolean isInitiated;

    public AnnotatedViewController(final View viewData, final Object wrappedObject, final AssetService assetService) {
        super(wrappedObject);
        this.viewData = viewData;

        id = Strings.isWhitespace(viewData.id()) ? wrappedObject.getClass().getSimpleName() : viewData.id();

        loadThemes(assetService);
    }

    private void loadThemes(final AssetService assetService) {
        if (viewData.themes().length > 0) {
            if (viewData.loadThemesEagerly()) {
                for (final String theme : viewData.themes()) {
                    themes.add(assetService.finishLoading(theme, Music.class));
                }
            } else {
                for (final String theme : viewData.themes()) {
                    assetService.load(theme, Music.class);
                }
                assetService.addOnLoadAction(new ThemesAssignmentAction(viewData.themes(), this, assetService));
            }
        }
    }

    @Override
    public void createView(final InterfaceService interfaceService) {
        initiate();
        stage = new Stage(interfaceService.getViewportProvider().provide(), interfaceService.getBatch());
        injectStage(stage);
        final Array<Actor> actors = interfaceService.getParser().createView(wrappedObject,
                Gdx.files.getFileHandle(viewData.value(), viewData.fileType()));
        LmlUtilities.appendActorsToStage(stage, actors);
        if (viewInitializer != null) {
            viewInitializer.initialize(stage, interfaceService.getParser().getActorsMappedByIds());
        }
        if (viewData.clearLmlMetaData()) {
            LmlUtilities.clearLmlUserObjects(actors);
        }
    }

    private ViewRenderer createRenderer() {
        if (ViewRenderer.class.equals(viewData.renderer())) {
            if (wrappedObject instanceof ViewRenderer) {
                return (ViewRenderer) wrappedObject;
            }
            return InterfaceService.DEFAULT_VIEW_RENDERER;
        } else if (wrappedObject.getClass().equals(viewData.renderer())) {
            return (ViewRenderer) wrappedObject; // Safe to cast, wrapped has to implement VR.
        }
        return Reflection.newInstance(viewData.renderer());
    }

    private ViewResizer createResizer() {
        if (ViewResizer.class.equals(viewData.resizer())) {
            if (wrappedObject instanceof ViewResizer) {
                return (ViewResizer) wrappedObject;
            }
            return InterfaceService.DEFAULT_VIEW_RESIZER;
        } else if (wrappedObject.getClass().equals(viewData.resizer())) {
            return (ViewResizer) wrappedObject; // Safe to cast, wrapped has to implement VR.
        }
        return Reflection.newInstance(viewData.resizer());
    }

    private ViewPauser createPauser() {
        if (ViewPauser.class.equals(viewData.pauser())) {
            if (wrappedObject instanceof ViewPauser) {
                return (ViewPauser) wrappedObject;
            }
            return InterfaceService.DEFAULT_VIEW_PAUSER;
        } else if (wrappedObject.getClass().equals(viewData.pauser())) {
            return (ViewPauser) wrappedObject; // Safe to cast, wrapped has to implement VP.
        }
        return Reflection.newInstance(viewData.pauser());
    }

    private ViewShower createShower() {
        if (ViewShower.class.equals(viewData.shower())) {
            if (wrappedObject instanceof ViewShower) {
                return (ViewShower) wrappedObject;
            }
            return InterfaceService.DEFAULT_VIEW_SHOWER;
        } else if (wrappedObject.getClass().equals(viewData.shower())) {
            return (ViewShower) wrappedObject; // Safe to cast, wrapped has to implement VS.
        }
        return Reflection.newInstance(viewData.shower());
    }

    private ViewInitializer createInitializer() {
        if (ViewInitializer.class.equals(viewData.initializer())) {
            if (wrappedObject instanceof ViewInitializer) {
                return (ViewInitializer) wrappedObject;
            }
            return InterfaceService.DEFAULT_VIEW_INITIALIZER;
        } else if (wrappedObject.getClass().equals(viewData.initializer())) {
            return (ViewInitializer) wrappedObject; // Safe to cast, wrapped has to implement VI.
        }
        return Reflection.newInstance(viewData.initializer());
    }

    private void initiate() {
        if (!isInitiated) {
            isInitiated = true;
            viewRenderer = createRenderer();
            viewResizer = createResizer();
            viewPauser = createPauser();
            viewShower = createShower();
            viewInitializer = createInitializer();
        }
    }

    @Override
    public boolean isCreated() {
        return stage != null;
    }

    @Override
    public void render(final float delta) {
        viewRenderer.render(stage, delta);
    }

    @Override
    public void resize(final int width, final int height) {
        viewResizer.resize(stage, width, height);
    }

    @Override
    public void pause() {
        if (viewPauser != null) {
            viewPauser.pause(this);
        }
    }

    @Override
    public void resume() {
        if (viewPauser != null) {
            viewPauser.resume(this);
        }
    }

    @Override
    public void show(final Action action) {
        viewShower.show(stage, action);
    }

    @Override
    public void hide(final Action action) {
        viewShower.hide(stage, action);
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public String getViewId() {
        return id;
    }

    @Override
    public ActionContainer getActionContainer() {
        if (wrappedObject instanceof ActionContainer) {
            return (ActionContainer) wrappedObject;
        }
        return null;
    }

    @Override
    public boolean isFirst() {
        return viewData.first();
    }

    @Override
    public void destroyView() {
        Disposables.disposeOf(stage);
        if (viewInitializer != null) {
            viewInitializer.destroy(this);
        }
        stage = null;
        clearStage();
    }

    @Override
    public Array<Music> getThemes() {
        return themes;
    }

    @Override
    public Music getNextTheme() {
        if (GdxArrays.isEmpty(themes)) {
            return null;
        } else if (GdxArrays.sizeOf(themes) == 1) {
            return themes.first();
        }
        final Music nextTheme = themes.get(currentThemeIndex);
        currentThemeIndex = viewData.themeOrdering().getNextIndex(themes, currentThemeIndex);
        return nextTheme;
    }
}
