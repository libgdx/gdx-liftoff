package com.github.czyzby.autumn.mvc.application;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;
import com.github.czyzby.autumn.mvc.component.i18n.LocaleService;
import com.github.czyzby.autumn.mvc.component.i18n.processor.AvailableLocalesAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.i18n.processor.I18nBundleAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.preferences.PreferencesService;
import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.autumn.mvc.component.sfx.processor.MusicEnabledAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.sfx.processor.MusicVolumeAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.sfx.processor.SoundEnabledAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.sfx.processor.SoundVolumeAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.SkinService;
import com.github.czyzby.autumn.mvc.component.ui.processor.LmlMacroAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.ui.processor.LmlParserSyntaxAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.ui.processor.PreferenceAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.ui.processor.SkinAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.ui.processor.SkinAssetAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.ui.processor.StageViewportAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.ui.processor.ViewActionContainerAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.ui.processor.ViewAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.ui.processor.ViewDialogAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.ui.processor.ViewStageAnnotationProcessor;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.autumn.mvc.stereotype.ViewActionContainer;
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
import com.github.czyzby.autumn.mvc.stereotype.preference.Property;
import com.github.czyzby.autumn.mvc.stereotype.preference.StageViewport;
import com.github.czyzby.autumn.scanner.ClassScanner;
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.tuple.immutable.Pair;

/** Default application listener implementation for Autumn MVC applications. Manages context.
 *
 * @author MJ */
public class AutumnApplication implements ApplicationListener {
    private Array<Pair<Class<?>, ClassScanner>> componentScanners;
    private ContextDestroyer contextDestroyer;
    private InterfaceService interfaceService;
    private boolean createMissingDependencies = true;

    public AutumnApplication(final ClassScanner componentScanner, final Class<?> scanningRoot) {
        componentScanners = GdxArrays.newArray();
        registerComponents(componentScanner, scanningRoot);
    }

    /** Can be called only before {@link #create()} is invoked.
     *
     * @param componentScanner used to scan for annotated classes.
     * @param scanningRoot root of the scanning. */
    protected void registerComponents(final ClassScanner componentScanner, final Class<?> scanningRoot) {
        componentScanners.add(new Pair<Class<?>, ClassScanner>(scanningRoot, componentScanner));
    }

    @Override
    public void create() {
        initiateContext();
        clearComponentScanners();
    }

    private void initiateContext() {
        interfaceService = new InterfaceService();
        final ContextInitializer initializer = new ContextInitializer();
        initializer.createMissingDependencies(createMissingDependencies);
        registerDefaultComponentAnnotations(initializer);
        addDefaultComponents(initializer);
        for (final Pair<Class<?>, ClassScanner> componentScanner : componentScanners) {
            initializer.scan(componentScanner.getFirst(), componentScanner.getSecond());
        }
        contextDestroyer = initializer.initiate();
    }

    /** @param createMissingDependencies if true, classes not available in context (unannotated) and without valid
     *            providers will be created with no-arg constructor if requested for injection. Defaults to true.
     * @return this, for chaining. */
    public AutumnApplication setCreateMissingDependencies(final boolean createMissingDependencies) {
        this.createMissingDependencies = createMissingDependencies;
        return this;
    }

    /** Invoked before context initiation.
     *
     * @param initializer should be used to register component annotations to scan for. */
    @SuppressWarnings("unchecked")
    protected void registerDefaultComponentAnnotations(final ContextInitializer initializer) {
        initializer.scanFor(ViewActionContainer.class, ViewDialog.class, View.class, StageViewport.class,
                Property.class);
    }

    /** Invoked before context initiation.
     *
     * @param initializer should be used to registered default components, created with plain old Java. */
    protected void addDefaultComponents(final ContextInitializer initializer) {
        initializer.addComponents(
                // PROCESSORS.
                // Assets:
                new AssetService(), new SkinAssetAnnotationProcessor(),
                // Locale:
                new LocaleService(),
                // SFX:
                new MusicEnabledAnnotationProcessor(), new MusicVolumeAnnotationProcessor(),
                new SoundEnabledAnnotationProcessor(), new SoundVolumeAnnotationProcessor(),
                // Settings:
                new I18nBundleAnnotationProcessor(), new PreferenceAnnotationProcessor(), new SkinAnnotationProcessor(),
                new StageViewportAnnotationProcessor(), new PreferencesService(),
                // Interface:
                new ViewAnnotationProcessor(), new ViewDialogAnnotationProcessor(),
                new ViewActionContainerAnnotationProcessor(), new ViewStageAnnotationProcessor(),
                new LmlMacroAnnotationProcessor(), new LmlParserSyntaxAnnotationProcessor(),
                new AvailableLocalesAnnotationProcessor(),
                // COMPONENTS.
                // SFX:
                new MusicService(),
                // Interface:
                interfaceService, new SkinService());
    }

    private void clearComponentScanners() {
        componentScanners.clear();
        componentScanners = null;
    }

    @Override
    public void resize(final int width, final int height) {
        interfaceService.resize(width, height);
    }

    @Override
    public void render() {
        GdxUtilities.clearScreen();
        interfaceService.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void pause() {
        interfaceService.pause();
    }

    @Override
    public void resume() {
        interfaceService.resume();
    }

    /** @return direct reference to main {@link InterfaceService} instance. After {@link #create()} method is executed,
     *         this value is never {@code null}. */
    public InterfaceService getInterfaceService() {
        return interfaceService;
    }

    @Override
    public void dispose() {
        Disposables.disposeOf(contextDestroyer);
    }
}
