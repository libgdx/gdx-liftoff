package gdx.liftoff.data.templates.unofficial

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.SourceFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.libraries.unofficial.LMLVis
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.platforms.Core
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.ProjectTemplate

/**
 * A solid application base using Autumn MVC.
 */
@ProjectTemplate
open class AutumnMvcVisTemplate : AutumnMvcBasicTemplate() {
  override val id = "lmlMvcVisTemplate"
  override val generateSkin = false
  override val description: String
    get() = "Project template included launchers with [Autumn](https://github.com/crashinvaders/gdx-lml/tree/master/autumn) " +
      "class scanners and a basic [Autumn MVC](https://github.com/czyzby/gdx-lml/tree/master/mvc) application."

  override fun getReflectedClasses(project: Project): Array<String> =
    arrayOf("com.github.czyzby.autumn.mvc.component.preferences.dto.AbstractPreference")

  override fun getReflectedPackages(project: Project): Array<String> =
    arrayOf(
      "${project.basic.rootPackage}.configuration",
      "${project.basic.rootPackage}.controller",
      "${project.basic.rootPackage}.service"
    )

  override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

/** This class serves only as the application scanning root. Any classes in its package (or any of the sub-packages)
 * with proper Autumn MVC annotations will be found, scanned and initiated. */
public class ${project.basic.mainClass} {
  /** Default application size. */
  public static final int WIDTH = 480, HEIGHT = 360;
}"""

  override fun apply(project: Project) {
    super.apply(project)
    addResources(project)
    addSources(project)
    // Adding VisUI support:
    LMLVis().initiate(project)
  }

  protected open fun addResources(project: Project) {
    // Adding resources:
    project.files.add(
      CopiedFile(
        projectName = Assets.ID,
        path = path("images", "libgdx.png"),
        original = path("generator", "templates", "libgdx.png")
      )
    )
    project.files.add(
      CopiedFile(
        projectName = Assets.ID,
        path = path("music", "theme.ogg"),
        original = path("generator", "templates", "autumn", "theme.ogg")
      )
    )
    // Adding I18N bundle:
    arrayOf("", "_en", "_pl").forEach {
      val fileName = "bundle$it.properties"
      project.files.add(
        CopiedFile(
          projectName = Assets.ID,
          path = path("i18n", fileName),
          original = path("generator", "templates", "autumn", fileName)
        )
      )
    }
  }

  protected open fun addSources(project: Project) {
    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.configuration",
        fileName = "Configuration.java",
        content = """package ${project.basic.rootPackage}.configuration;

  import com.badlogic.gdx.utils.viewport.FitViewport;
  import com.badlogic.gdx.utils.viewport.Viewport;
  import com.github.czyzby.autumn.annotation.Component;
  import com.github.czyzby.autumn.annotation.Initiate;
  import com.github.czyzby.autumn.mvc.component.ui.SkinService;
  import com.github.czyzby.autumn.mvc.stereotype.preference.AvailableLocales;
  import com.github.czyzby.autumn.mvc.stereotype.preference.I18nBundle;
  import com.github.czyzby.autumn.mvc.stereotype.preference.I18nLocale;
  import com.github.czyzby.autumn.mvc.stereotype.preference.LmlMacro;
  import com.github.czyzby.autumn.mvc.stereotype.preference.LmlParserSyntax;
  import com.github.czyzby.autumn.mvc.stereotype.preference.Preference;
  import com.github.czyzby.autumn.mvc.stereotype.preference.StageViewport;
  import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.MusicEnabled;
  import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.MusicVolume;
  import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.SoundEnabled;
  import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.SoundVolume;
  import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;
  import com.github.czyzby.lml.parser.LmlSyntax;
  import com.github.czyzby.lml.util.Lml;
  import com.github.czyzby.lml.vis.parser.impl.VisLmlSyntax;
  import com.kotcrab.vis.ui.VisUI;
  import ${project.basic.rootPackage}.${project.basic.mainClass};
  import ${project.basic.rootPackage}.service.ScaleService;

  /** Thanks to the Component annotation, this class will be automatically found and processed.
   *
   * This is a utility class that configures application settings. */
  @Component
  public class Configuration {
    /** Name of the application's preferences file. */
    public static final String PREFERENCES = "${project.basic.name}";
    /** Path to the internationalization bundle. */
    @I18nBundle private final String bundlePath = "i18n/bundle";
    /** Enabling VisUI usage. */
    @LmlParserSyntax private final LmlSyntax syntax = new VisLmlSyntax();
    /** Parsing macros available in all views. */
    @LmlMacro private final String globalMacro = "ui/templates/macros/global.lml";
    /** Using a custom viewport provider - Autumn MVC defaults to the ScreenViewport, as it is the only viewport that
     * doesn't need to know application's targeted screen size. This provider overrides that by using more sophisticated
     * FitViewport that works on virtual units rather than pixels. */
    @StageViewport private final ObjectProvider<Viewport> viewportProvider = new ObjectProvider<Viewport>() {
      @Override
      public Viewport provide() {
        return new FitViewport(${project.basic.mainClass}.WIDTH, ${project.basic.mainClass}.HEIGHT);
      }
    };

    /** These sound-related fields allow MusicService to store settings in preferences file. Sound preferences will be
     * automatically saved when the application closes and restored the next time it's turned on. Sound-related methods
     * methods will be automatically added to LML templates - see settings.lml template. */
    @SoundVolume(preferences = PREFERENCES) private final String soundVolume = "soundVolume";
    @SoundEnabled(preferences = PREFERENCES) private final String soundEnabled = "soundOn";
    @MusicVolume(preferences = PREFERENCES) private final String musicVolume = "musicVolume";
    @MusicEnabled(preferences = PREFERENCES) private final String musicEnabledPreference = "musicOn";

    /** These i18n-related fields will allow LocaleService to save game's locale in preferences file. Locale changing
     * actions will be automatically added to LML templates - see settings.lml template. */
    @I18nLocale(propertiesPath = PREFERENCES, defaultLocale = "en") private final String localePreference = "locale";
    @AvailableLocales private final String[] availableLocales = new String[] { "en" };

    /** Setting the default Preferences object path. */
    @Preference private final String preferencesPath = PREFERENCES;

    /** Thanks to the Initiate annotation, this method will be automatically invoked during context building. All
     * method's parameters will be injected with values from the context.
     *
     * @param scaleService contains current GUI scale.
     * @param skinService contains GUI skin. */
    @Initiate
    public void initiateConfiguration(final ScaleService scaleService, final SkinService skinService) {
      // Loading default VisUI skin with the selected scale:
      VisUI.load(scaleService.getScale());
      // Registering VisUI skin with "default" name - this skin will be the default one for all LML widgets:
          skinService.addSkin("default", VisUI.getSkin());
            // Thanks to this setting, only methods annotated with @LmlAction will be available in views, significantly
            // speeding up method look-up:
            Lml.EXTRACT_UNANNOTATED_METHODS = false;
        }
    }"""
      )
    )
    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.configuration.preferences",
        fileName = "ScalePreference.java",
        content = """package ${project.basic.rootPackage}.configuration.preferences;

    import com.badlogic.gdx.scenes.scene2d.Actor;
    import com.github.czyzby.autumn.mvc.component.preferences.dto.AbstractPreference;
    import com.github.czyzby.autumn.mvc.stereotype.preference.Property;
    import com.github.czyzby.lml.util.LmlUtilities;
    import com.kotcrab.vis.ui.VisUI.SkinScale;

    /** Thanks to the Property annotation, this class will be automatically found and initiated.
     *
     * This class manages VisUI scale preference. */
    @Property("Scale")
    public class ScalePreference extends AbstractPreference<SkinScale> {
        @Override
        public SkinScale getDefault() {
            return SkinScale.X2;
        }

        @Override
        public SkinScale extractFromActor(final Actor actor) {
            return convert(LmlUtilities.getActorId(actor));
        }

        @Override
        protected SkinScale convert(final String rawPreference) {
            return SkinScale.valueOf(rawPreference);
        }

        @Override
        protected String serialize(final SkinScale preference) {
            return preference.name();
        }
    }"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.controller",
        fileName = "LoadingController.java",
        content = """package ${project.basic.rootPackage}.controller;

    import com.badlogic.gdx.scenes.scene2d.Stage;
    import com.github.czyzby.autumn.annotation.Inject;
    import com.github.czyzby.autumn.mvc.component.asset.AssetService;
    import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
    import com.github.czyzby.autumn.mvc.stereotype.View;
    import com.github.czyzby.lml.annotation.LmlActor;
    import com.kotcrab.vis.ui.widget.VisProgressBar;

    /** Thanks to View annotation, this class will be automatically found and initiated.
     *
     * This is the first application's view, shown right after the application starts. It will hide after all assests are
     * loaded. */
    @View(value = "ui/templates/loading.lml", first = true)
    public class LoadingController implements ViewRenderer {
        /** Will be injected automatically. Manages assets. Used to display loading progress. */
        @Inject private AssetService assetService;
        /** This is a widget injected from the loading.lml template. "loadingBar" is its ID. */
        @LmlActor("loadingBar") private VisProgressBar loadingBar;

        // Since this class implements ViewRenderer, it can modify the way its view is drawn. Additionally to drawing the
        // stage, this view also updates assets manager and reads its progress.
        @Override
        public void render(final Stage stage, final float delta) {
            assetService.update();
            loadingBar.setValue(assetService.getLoadingProgress());
            stage.act(delta);
            stage.draw();
        }
    }"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.controller",
        fileName = "MenuController.java",
        content = """package ${project.basic.rootPackage}.controller;

    import com.badlogic.gdx.graphics.Texture;
    import com.badlogic.gdx.graphics.g2d.Batch;
    import com.badlogic.gdx.scenes.scene2d.Stage;
    import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
    import com.github.czyzby.autumn.mvc.stereotype.Asset;
    import com.github.czyzby.autumn.mvc.stereotype.View;

    /** Thanks to View annotation, this class will be automatically found and initiated.
     *
     * This is application's main view, displaying a menu with several options. */
    @View(id = "menu", value = "ui/templates/menu.lml", themes = "music/theme.ogg")
    public class MenuController implements ViewRenderer {
        /** Asset-annotated files will be found and automatically loaded by the AssetsService. */
        @Asset("images/libgdx.png") private Texture logo;

        @Override
        public void render(final Stage stage, final float delta) {
            // As a proof of concept that you can pair custom logic with Autumn MVC views, this class implements
            // ViewRenderer and handles view rendering manually. It renders libGDX logo before drawing the stage.
            stage.act(delta);

            final Batch batch = stage.getBatch();
            batch.setColor(stage.getRoot().getColor()); // We want the logo to share color alpha with the stage.
            batch.begin();
            batch.draw(logo, (int) (stage.getWidth() - logo.getWidth()) / 2,
                    (int) (stage.getHeight() - logo.getHeight()) / 2);
            batch.end();

            stage.draw();
        }
    }"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.controller.action",
        fileName = "Global.java",
        content = """package ${project.basic.rootPackage}.controller.action;

    import com.github.czyzby.autumn.mvc.stereotype.ViewActionContainer;
    import com.github.czyzby.lml.annotation.LmlAction;
    import com.github.czyzby.lml.parser.action.ActionContainer;

    /** Since this class implements ActionContainer and is annotated with ViewActionContainer, its methods will be reflected
     * and available in all LML templates. Note that this class is a component like any other, so it can inject any fields,
     * use Initiate-annotated methods, etc. */
    @ViewActionContainer("global")
    public class Global implements ActionContainer {
        /** This is a mock-up method that does nothing. It will be available in LML templates through "close" (annotation
         * argument) and "noOp" (method name) IDs. */
        @LmlAction("close")
        public void noOp() {
        }
    }"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.controller.dialog",
        fileName = "SettingsController.java",
        content = """package ${project.basic.rootPackage}.controller.dialog;

    import com.badlogic.gdx.scenes.scene2d.Actor;
    import com.github.czyzby.autumn.annotation.Inject;
    import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
    import com.github.czyzby.lml.annotation.LmlAction;
    import com.github.czyzby.lml.parser.action.ActionContainer;
    import com.kotcrab.vis.ui.VisUI.SkinScale;
    import ${project.basic.rootPackage}.service.ScaleService;

    /** This is a settings dialog, which can be shown in any view by using "show:settings" LML action or - in Java code -
     * through InterfaceService.showDialog(Class) method. Thanks to the fact that it implements ActionContainer, its methods
     * will be available in the LML template. */
    @ViewDialog(id = "settings", value = "ui/templates/dialogs/settings.lml")
    public class SettingsController implements ActionContainer {
        // @Inject-annotated fields will be automatically filled with values from the context.
        @Inject private ScaleService scaleService;

        /** @return array of available GUI scales. */
        @LmlAction("scales")
        public SkinScale[] getGuiScales() {
            return scaleService.getScales();
        }

        /** @param actor requested scale change. Its ID represents a GUI scale. */
        @LmlAction("changeScale")
        public void changeGuiScale(final Actor actor) {
            final SkinScale scale = scaleService.getPreference().extractFromActor(actor);
            scaleService.changeScale(scale);
        }
    }"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.service",
        fileName = "ScaleService.java",
        content = """package ${project.basic.rootPackage}.service;

    import com.github.czyzby.autumn.annotation.Component;
    import com.github.czyzby.autumn.annotation.Inject;
    import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
    import com.github.czyzby.autumn.mvc.component.ui.SkinService;
    import com.kotcrab.vis.ui.VisUI;
    import com.kotcrab.vis.ui.VisUI.SkinScale;
    import ${project.basic.rootPackage}.configuration.preferences.ScalePreference;

    /** Thanks to the ViewActionContainer annotation, this class will be automatically found and processed.
     *
     * This service handles GUI scale. */
    @Component
    public class ScaleService {
        // @Inject-annotated fields will be automatically filled by the context initializer.
        @Inject private ScalePreference preference;
        @Inject private InterfaceService interfaceService;
        @Inject private SkinService skinService;

        /** @return current GUI scale. */
        public SkinScale getScale() {
            return preference.get();
        }

        /** @return all scales supported by the application. */
        public SkinScale[] getScales() {
            return SkinScale.values();
        }

        /** @return scale property, which is saved in application's preferences. */
        public ScalePreference getPreference() {
            return preference;
        }

        /** @param scale the new application's scale. */
        public void changeScale(final SkinScale scale) {
            if (preference.get() == scale) {
                return; // This is the current scale.
            }
            preference.set(scale);
            // Changing GUI skin, reloading all screens:
            interfaceService.reload(new Runnable() {
                @Override
                public void run() {
                    // Removing previous skin resources:
                    VisUI.dispose();
                    // Loading new skin:
                    VisUI.load(scale);
                    // Replacing the previously default skin:
                    skinService.clear();
                    skinService.addSkin("default", VisUI.getSkin());
                }
            });
        }
    }"""
      )
    )
  }

  override fun addViews(project: Project) {
    project.files.add(
      SourceFile(
        projectName = Assets.ID,
        sourceFolderPath = "ui",
        packageName = "templates",
        fileName = "loading.lml",
        content = """<!-- Going through LML tutorials is suggested before starting with Autumn MVC. If anything is unclear
    in the .lml files, you should go through LML resources first. -->
<window title="@loadingTitle" titleAlign="center">
    <!-- Thanks to "goto:menu" action, menu.lml will be shown after this bar is fully loaded. -->
    <progressBar id="loadingBar" animateDuration="0.4" onComplete="goto:menu"/>
</window>"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Assets.ID,
        sourceFolderPath = "ui",
        packageName = "templates",
        fileName = "menu.lml",
        content = """<table oneColumn="true" defaultPad="2" tableAlign="bottomRight" fillParent="true" defaultFillX="true">
    <!-- "show:settings" will automatically show the settings.lml dialog when button is clicked. -->
    <textButton onChange="show:settings">@settings</textButton>
    <!-- "app:exit" will automatically try to exit the application when the button is clicked. -->
    <textButton onChange="app:exit">@exit</textButton>
</table>"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Assets.ID,
        sourceFolderPath = "ui",
        packageName = "templates",
        fileName = path("dialogs", "settings.lml"),
        content = """<dialog id="dialog" title="@settings" style="dialog">
    <!-- Note that all values (like width and height) are in viewport units, not pixels.
        Its somewhat safe to use "magic" values. Values in {= } are equations; values
        proceeded with $ reference Java methods. -->
    <tabbedPane selected="0" width="{=200 * (${'$'}getScale - X)}" height="{=100 * (${'$'}getScale - X)}">
        <!-- :setting macro is defined at global.lml. -->
        <:setting name="@music">
            @musicVolume
            <!-- Music-related methods are added by MusicService. -->
            <slider value="${'$'}getMusicVolume" onChange="setMusicVolume" growX="true" />
            <checkBox onChange="toggleMusic" checked="${'$'}musicOn">@toggleMusic</checkBox>
        </:setting>
        <:setting name="@sound">
            @soundVolume
            <!-- Sound-related methods are added by MusicService. -->
            <slider value="${'$'}getSoundVolume" onChange="setSoundVolume" growX="true" />
            <checkBox onChange="toggleSound" checked="${'$'}soundOn">@toggleSound</checkBox>
        </:setting>
        <:setting name="@locale">
        <!-- {locales} and {currentLocale} are LML arguments automatically added by
            LocaleService. "locale:name" action changes current locale and reloads UI.
            For example, "locale:en" action would change current locale to English. -->
            <:each locale="{locales}">
                <:if test="{locale} != {currentLocale}">
                    <textButton onChange="locale:{locale}">@{locale}</textButton>
                </:if>
            </:each>
        </:setting>
        <:setting name="@gui">
            @scale
            <!-- Scale-related actions are registered by SettingsController and handled by our
                custom ScaleService. -->
            <:each scale="${'$'}scales">
                <:if test="{scale} != ${'$'}getScale">
                    <textButton id="{scale}" onChange="changeScale">{scale}</textButton>
                </:if>
            </:each>
        </:setting>
    </tabbedPane>
    <!-- "close" action is defined in Global class. -->
    <textButton onResult="close">@exit</textButton>
</dialog>"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Assets.ID,
        sourceFolderPath = "ui",
        packageName = "templates",
        fileName = path("macros", "global.lml"),
        content = """<!-- This is a custom macro that displays a TabbedPane's tab.
    - name: becomes the title of the tab. Defaults to empty string. -->
<:macro alias="setting" replace="content" name="">
<!-- "name" will be replaced with the value of the passed argument. -->
<tab title="{name}" closeable="false" oneColumn="true" defaultPad="1" bg="dialogDim">
    <!-- "content" will be replaced with the data between macro tags. -->
    {content}
</tab>
</:macro>"""
      )
    )
  }
}
