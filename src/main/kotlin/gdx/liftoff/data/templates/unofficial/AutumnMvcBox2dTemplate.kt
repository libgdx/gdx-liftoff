package gdx.liftoff.data.templates.unofficial

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.SourceFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.libraries.official.Box2D
import gdx.liftoff.data.libraries.official.Controllers
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.platforms.Core
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.ProjectTemplate

/**
 * Advanced Autumn MVC template showing VisUI, Controllers and Box2D usage.
 */
@ProjectTemplate
@Suppress("unused") // Referenced via reflection.
class AutumnMvcBox2dTemplate : AutumnMvcVisTemplate() {
  override val id = "lmlMvcBox2dTemplate"
  override val description: String
    get() = "Project template included launchers with [Autumn](https://github.com/crashinvaders/gdx-lml/tree/master/autumn) " +
      "class scanners and an [Autumn MVC](https://github.com/czyzby/gdx-lml/tree/master/mvc) application " +
      "showing usage of Box2D and Controllers libGDX extensions. A simple GUI consisting of several screens " +
      "and dialogs was provided, including a settings view that allows the players to choose their controls."

  override fun apply(project: Project) {
    super.apply(project)
    // Adding extra dependencies:
    Box2D().initiate(project)
    Controllers().initiate(project)
  }

  override fun addResources(project: Project) {
    // Adding music theme:
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
          original = path("generator", "templates", "autumn", "box2d", fileName)
        )
      )
    }
    // Adding LML views:
    arrayOf("game.lml", "loading.lml", "menu.lml").forEach {
      project.files.add(
        CopiedFile(
          projectName = Assets.ID,
          path = path("ui", "templates", it),
          original = path("generator", "templates", "autumn", "box2d", it)
        )
      )
    }
    arrayOf("controls.lml", "edit.lml", "inactive.lml", "settings.lml", "switch.lml").forEach {
      project.files.add(
        CopiedFile(
          projectName = Assets.ID,
          path = path("ui", "templates", "dialogs", it),
          original = path("generator", "templates", "autumn", "box2d", "dialogs", it)
        )
      )
    }
    project.files.add(
      CopiedFile(
        projectName = Assets.ID,
        path = path("ui", "templates", "macros", "global.lml"),
        original = path("generator", "templates", "autumn", "box2d", "macros", "global.lml")
      )
    )
  }

  override fun addSources(project: Project) {
    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.configuration",
        fileName = "Configuration.java",
        content = """package ${project.basic.rootPackage}.configuration;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.SkinService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewResizer;
import com.github.czyzby.autumn.mvc.stereotype.preference.AvailableLocales;
import com.github.czyzby.autumn.mvc.stereotype.preference.I18nBundle;
import com.github.czyzby.autumn.mvc.stereotype.preference.I18nLocale;
import com.github.czyzby.autumn.mvc.stereotype.preference.LmlMacro;
import com.github.czyzby.autumn.mvc.stereotype.preference.LmlParserSyntax;
import com.github.czyzby.autumn.mvc.stereotype.preference.Preference;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.MusicEnabled;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.MusicVolume;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.SoundEnabled;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.SoundVolume;
import com.github.czyzby.kiwi.util.gdx.scene2d.Actors;
import com.github.czyzby.lml.parser.LmlSyntax;
import com.github.czyzby.lml.vis.parser.impl.VisLmlSyntax;
import com.kotcrab.vis.ui.VisUI;

/** Thanks to the Component annotation, this class will be automatically found and processed.
 *
 * This is a utility class that configures application settings. */
@Component
public class Configuration {
  /** Name of the application's preferences file. */
  public static final String PREFERENCES = "${project.basic.name}";
  /** Max players amount. */
  public static final int PLAYERS_AMOUNT = 3;
  /** Path to global macro file. */
  @LmlMacro private final String globalMacro = "ui/templates/macros/global.lml";
  /** Path to the internationalization bundle. */
  @I18nBundle private final String bundlePath = "i18n/bundle";
  /** Enabling VisUI usage. */
  @LmlParserSyntax private final LmlSyntax syntax = new VisLmlSyntax();

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
   * @param skinService contains GUI skin. */
  @Initiate
  public void initiateConfiguration(final SkinService skinService) {
    // Loading default VisUI skin with the selected scale:
    VisUI.load(VisUI.SkinScale.X2);
    // Registering VisUI skin with "default" name - this skin will be the default one for all LML widgets:
    skinService.addSkin("default", VisUI.getSkin());
    // Changing the default resizer - centering actors on resize.
    InterfaceService.DEFAULT_VIEW_RESIZER = new ViewResizer() {
      @Override
      public void resize(final Stage stage, final int width, final int height) {
        stage.getViewport().update(width, height, true);
        for (final Actor actor : stage.getActors()) {
          Actors.centerActor(actor);
        }
      }
    };
  }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.configuration.preferences",
        fileName = "ControlsData.java",
        content = """package ${project.basic.rootPackage}.configuration.preferences;

import ${project.basic.rootPackage}.service.controls.ControlType;

/** JSON-encoded class. Uses public fields to support libGDX JSON utilities. */
public class ControlsData {
  /** Up movement shortcut. */
  public int up;
  /** Down movement shortcut. */
  public int down;
  /** Left movement shortcut. */
  public int left;
  /** Right movement shortcut. */
  public int right;
  /** Jump shortcut. */
  public int jump;
  /** Type of controls */
  public ControlType type;
  /** Additional data. Might be used for device ID. */
  public int index;
  /** Optional settings. Might not be supported by every controller. */
  public boolean invertX, invertY, invertXY;

  public ControlsData() {
  }

  public ControlsData(final ControlType type) {
    this.type = type;
  }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.configuration.preferences",
        fileName = "ControlsPreference.java",
        content = """package ${project.basic.rootPackage}.configuration.preferences;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.github.czyzby.autumn.mvc.component.preferences.dto.AbstractPreference;
import com.github.czyzby.autumn.mvc.stereotype.preference.Property;
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import ${project.basic.rootPackage}.configuration.Configuration;
import ${project.basic.rootPackage}.service.controls.ControlType;
import ${project.basic.rootPackage}.service.controls.impl.KeyboardControl;
import ${project.basic.rootPackage}.service.controls.impl.TouchControl;

/** Allows to save controls in preferences. */
@Property("Controls")
public class ControlsPreference extends AbstractPreference<Array<ControlsData>> {
  private final Json json = new Json();

    @Override
    public Array<ControlsData> getDefault() {
        final Array<ControlsData> controls = GdxArrays.newArray();
        // First player defaults to touch (on mobile) or keyboard (on desktop) controls.
        controls.add(GdxUtilities.isMobile() ? new TouchControl().toData() : new KeyboardControl().toData());
        for (int index = 1; index < Configuration.PLAYERS_AMOUNT; index++) {
            // Other players are simply inactive:
            controls.add(new ControlsData(ControlType.INACTIVE));
        }
        return controls;
    }

    @Override
    public Array<ControlsData> extractFromActor(final Actor actor) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Array<ControlsData> convert(final String rawPreference) {
        return json.fromJson(Array.class, ControlsData.class, Base64Coder.decodeString(rawPreference));
    }

    @Override
    protected String serialize(final Array<ControlsData> preference) {
        return Base64Coder.encodeString(json.toJson(preference, Array.class, ControlsData.class));
    }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.controller",
        fileName = "GameController.java",
        content = """package ${project.basic.rootPackage}.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewResizer;
import com.github.czyzby.autumn.mvc.component.ui.controller.impl.StandardViewShower;
import com.github.czyzby.autumn.mvc.stereotype.View;
import ${project.basic.rootPackage}.service.Box2DService;

/** Renders Box2D world. */
@View(id = "game", value = "ui/templates/game.lml", themes = "music/theme.ogg")
public class GameController extends StandardViewShower implements ViewResizer, ViewRenderer {
    @Inject private Box2DService box2d;
    private final Box2DDebugRenderer renderer = new Box2DDebugRenderer();

    @Override
    public void show(final Stage stage, final Action action) {
        box2d.create();
        super.show(stage, Actions.sequence(action, Actions.run(new Runnable() {
            @Override
            public void run() { // Listening to user input events:
                final InputMultiplexer inputMultiplexer = new InputMultiplexer(stage);
                box2d.initiateControls(inputMultiplexer);
                Gdx.input.setInputProcessor(inputMultiplexer);
            }
        })));
    }

    @Override
    public void resize(final Stage stage, final int width, final int height) {
        box2d.resize(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(final Stage stage, final float delta) {
        box2d.update(delta);
        renderer.render(box2d.getWorld(), box2d.getViewport().getCamera().combined);
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

import com.badlogic.gdx.utils.Array;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.action.ActionContainer;
import ${project.basic.rootPackage}.controller.dialog.NotEnoughPlayersErrorController;
import ${project.basic.rootPackage}.service.ControlsService;
import ${project.basic.rootPackage}.service.controls.Control;

/** Thanks to View annotation, this class will be automatically found and initiated.
 *
 * This is application's main view, displaying a menu with several options. */
@View(id = "menu", value = "ui/templates/menu.lml", themes = "music/theme.ogg")
public class MenuController implements ActionContainer {
    @Inject private InterfaceService interfaceService;
    @Inject private ControlsService controlsService;

    @LmlAction("startGame")
    public void startPlaying() {
        if (isAnyPlayerActive()) {
            interfaceService.show(GameController.class);
        } else {
            interfaceService.showDialog(NotEnoughPlayersErrorController.class);
        }
    }

    private boolean isAnyPlayerActive() {
        final Array<Control> controls = controlsService.getControls();
        for (final Control control : controls) {
            if (control.isActive()) {
                return true;
            }
        }
        return false;
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
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.action.ActionContainer;
import ${project.basic.rootPackage}.configuration.Configuration;

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

    /** @return true if the game is currently running on a mobile platform. */
    @LmlAction("isMobile")
    public boolean isOnMobilePlatform() {
        return GdxUtilities.isMobile();
    }

    /** @return total amount of playable characters. */
    @LmlAction("playersAmount")
    public int getPlayersAmount() {
        return Configuration.PLAYERS_AMOUNT;
    }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.controller.dialog",
        fileName = "ControlsController.java",
        content = """package ${project.basic.rootPackage}.controller.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewDialogShower;
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.util.LmlUtilities;
import ${project.basic.rootPackage}.configuration.Configuration;
import ${project.basic.rootPackage}.service.ControlsService;
import ${project.basic.rootPackage}.service.controls.Control;

/** Allows to set up player controls. */
@ViewDialog(id = "controls", value = "ui/templates/dialogs/controls.lml", cacheInstance = true)
public class ControlsController implements ActionContainer, ViewDialogShower {
    @Inject ControlsService service;
    @Inject ControlsEditController controlsEdit;
    @Inject ControlsSwitchController controlsSwitch;
    @Inject InterfaceService interfaceService;
    /** Controller edition buttons mapped by their in-view IDs. */
    @LmlActor("edit[0," + (Configuration.PLAYERS_AMOUNT - 1) + "]") private ObjectMap<String, Button> editButtons;

    @Override
    public void doBeforeShow(final Window dialog) {
        final Array<Control> controls = service.getControls();
        for (int index = 0; index < Configuration.PLAYERS_AMOUNT; index++) {
            refreshPlayerView(index, controls.get(index));
        }
    }

    /** @param control belongs to the player. Should be called after the control is switched.
     * @param playerId ID of the player to refresh. */
    public void refreshPlayerView(final int playerId, final Control control) {
        final String editId = "edit" + playerId;
        if (control.isActive()) {
            editButtons.get(editId).setDisabled(false);
        } else {
            editButtons.get(editId).setDisabled(true);
        }
    }

    @LmlAction("edit")
    public void editControls(final Actor actor) {
        final int playerId = Integer.parseInt(LmlUtilities.getActorId(actor).replace("edit", ""));
        controlsEdit.setControl(service.getControl(playerId));
        interfaceService.showDialog(ControlsEditController.class);
    }

    @LmlAction("switch")
    public void switchControls(final Actor actor) {
        final int playerId = Integer.parseInt(LmlUtilities.getActorId(actor).replace("switch", ""));
        controlsSwitch.setPlayerId(playerId);
        interfaceService.showDialog(ControlsSwitchController.class);
    }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.controller.dialog",
        fileName = "ControlsEditController.java",
        content = """package ${project.basic.rootPackage}.controller.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewDialogShower;
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
import com.github.czyzby.autumn.mvc.stereotype.ViewStage;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.scene2d.range.FloatRange;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;
import ${project.basic.rootPackage}.service.controls.Control;
import ${project.basic.rootPackage}.service.controls.ControlListener;
import ${project.basic.rootPackage}.service.controls.ControlType;
import ${project.basic.rootPackage}.service.controls.impl.GamePadControl;
import ${project.basic.rootPackage}.service.controls.impl.KeyboardControl;
import com.kotcrab.vis.ui.widget.VisSelectBox;

/** Allows to edit chosen controls. */
@ViewDialog(id = "edit", value = "ui/templates/dialogs/edit.lml", cacheInstance = true)
public class ControlsEditController implements ActionContainer, ViewDialogShower {
    @ViewStage private Stage stage;
    private Control control;

    @LmlActor("mock") private Image mockUpEntity;
    @LmlActor("mainTable") private Table mainTable;
    @LmlActor("TOUCH;KEYBOARD;PAD") private ObjectMap<String, Actor> views;
    private TextButton checkedButton;
    private final MockUpdateAction updateAction = new MockUpdateAction();

    // Keyboard widgets:
    @LmlActor("keyUp") private TextButton keyUp;
    @LmlActor("keyDown") private TextButton keyDown;
    @LmlActor("keyLeft") private TextButton keyLeft;
    @LmlActor("keyRight") private TextButton keyRight;
    @LmlActor("keyJump") private TextButton keyJump;
    private final Actor keyboardListener = new Actor();

    // Game pad widgets:
    @LmlActor("padUp") private TextButton padUp;
    @LmlActor("padDown") private TextButton padDown;
    @LmlActor("padLeft") private TextButton padLeft;
    @LmlActor("padRight") private TextButton padRight;
    @LmlActor("padJump") private TextButton padJump;
    private final ControllerListener controllerListener;
    @LmlActor("invertX") private Button invertXButton;
    @LmlActor("invertY") private Button invertYButton;
    @LmlActor("invertXY") private Button invertXYButton;
    @LmlActor("controllers") private VisSelectBox<String> controllersSelect;
    private Array<Controller> controllers;

    public ControlsEditController() {
        // Allows to change current keyboard controls:
        keyboardListener.addListener(new InputListener() {
            @Override
            public boolean keyUp(final InputEvent event, final int keycode) {
                if (checkedButton == null) {
                    keyboardListener.remove();
                    return false;
                }
                final KeyboardControl keyboardControl = (KeyboardControl) control;
                if (checkedButton == keyUp) {
                    keyboardControl.setUp(keycode);
                } else if (checkedButton == keyDown) {
                    keyboardControl.setDown(keycode);
                } else if (checkedButton == keyLeft) {
                    keyboardControl.setLeft(keycode);
                } else if (checkedButton == keyRight) {
                    keyboardControl.setRight(keycode);
                } else if (checkedButton == keyJump) {
                    keyboardControl.setJump(keycode);
                }
                checkedButton.setText(Keys.toString(keycode));
                checkedButton.setChecked(false);
                checkedButton = null;
                keyboardListener.remove();
                return false;
            }
        });

        // Allows to change controller shortcuts:
        controllerListener = new ControllerAdapter() {
            @Override
            public boolean buttonUp(final Controller controller, final int buttonIndex) {
                if (checkedButton == null) {
                    controller.removeListener(controllerListener);
                    return false;
                }
                final GamePadControl keyboardControl = (GamePadControl) control;
                if (checkedButton == padUp) {
                    keyboardControl.setUp(buttonIndex);
                } else if (checkedButton == padDown) {
                    keyboardControl.setDown(buttonIndex);
                } else if (checkedButton == padLeft) {
                    keyboardControl.setLeft(buttonIndex);
                } else if (checkedButton == padRight) {
                    keyboardControl.setRight(buttonIndex);
                } else if (checkedButton == padJump) {
                    keyboardControl.setJump(buttonIndex);
                }
                checkedButton.setText(String.valueOf(buttonIndex));
                checkedButton.setChecked(false);
                checkedButton = null;
                controller.removeListener(controllerListener);
                return false;
            }
        };
    }

    /** @param control will be edited by this screen. */
    public void setControl(final Control control) {
        this.control = control;
    }

    @Override
    public void doBeforeShow(final Window dialog) {
        attachListeners();
        setCurrentControls();
        changeView();
        updateAction.reset();
        mockUpEntity.setColor(Color.WHITE);
        mockUpEntity.addAction(Actions.forever(updateAction));
    }

    private void attachListeners() {
        // Allowing controls to listen to input:
        final InputMultiplexer inputMultiplexer = new InputMultiplexer();
        control.attachInputListener(inputMultiplexer);
        control.setControlListener(new ControlListener() {
            @Override
            public void jump() {
                mockUpEntity.addAction(Actions.sequence(Actions.fadeOut(0.1f), Actions.fadeIn(0.1f)));
            }
        });
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void setCurrentControls() {
        if (control.getType() == ControlType.KEYBOARD) {
            final KeyboardControl keyboardControl = (KeyboardControl) control;
            keyUp.setText(Keys.toString(keyboardControl.getUp()));
            keyDown.setText(Keys.toString(keyboardControl.getDown()));
            keyLeft.setText(Keys.toString(keyboardControl.getLeft()));
            keyRight.setText(Keys.toString(keyboardControl.getRight()));
            keyJump.setText(Keys.toString(keyboardControl.getJump()));
        } else if (control.getType() == ControlType.PAD) {
            final GamePadControl gamePadControl = (GamePadControl) control;
            padUp.setText(String.valueOf(gamePadControl.getUp()));
            padDown.setText(String.valueOf(gamePadControl.getDown()));
            padLeft.setText(String.valueOf(gamePadControl.getLeft()));
            padRight.setText(String.valueOf(gamePadControl.getRight()));
            padJump.setText(String.valueOf(gamePadControl.getJump()));
            invertXButton.setChecked(gamePadControl.isInvertX());
            invertYButton.setChecked(gamePadControl.isInvertY());
            invertXYButton.setChecked(gamePadControl.isInvertXY());
            // Allowing the player to choose controller device:
            controllersSelect.getItems().clear();
            controllersSelect.getSelection().setMultiple(false);
            controllersSelect.getSelection().setRequired(true);
            controllers = Controllers.getControllers();
            final String[] items = new String[controllers.size];
            for (int index = 0; index < controllers.size; index++) {
                final Controller controller = controllers.get(index);
                items[index] = controller.getName().replaceAll(Strings.WHITESPACE_SPLITTER_REGEX, " ");
            }
            controllersSelect.setItems(items);
            final int controllerIndex = controllers.indexOf(gamePadControl.getController(), true);
            controllersSelect.setSelectedIndex(controllerIndex < 0 ? 0 : controllerIndex);
        }
    }

    private void changeView() {
        mainTable.clearChildren();
        // Finding view relevant to the controls:
        final Actor view = views.get(control.getType().name());
        mainTable.add(view).grow();
        mainTable.pack();
    }

    @LmlAction("hide")
    public void hide() {
        mockUpEntity.clearActions();
        keyboardListener.remove();
        if (checkedButton != null) {
            checkedButton.setChecked(false);
            checkedButton = null;
        }
        if (control.getType() == ControlType.PAD) {
            ((GamePadControl) control).getController().removeListener(controllerListener);
        }
        Gdx.input.setInputProcessor(stage);
    }

    @LmlAction("setKey")
    public void setKeyboardShortcut(final TextButton button) {
        if (button.isChecked()) {
            if (checkedButton != null) {
                checkedButton.setChecked(false);
            }
            checkedButton = button;
            stage.addActor(keyboardListener);
            stage.setKeyboardFocus(keyboardListener);
        } else {
            checkedButton = null;
            keyboardListener.remove();
        }
    }

    @LmlAction("setPad")
    public void setGamePadShortcut(final TextButton button) {
        final GamePadControl gamePadControl = (GamePadControl) control;
        if (button.isChecked()) {
            if (checkedButton != null) {
                checkedButton.setChecked(false);
            }
            checkedButton = button;
            gamePadControl.getController().addListener(controllerListener);
        } else {
            checkedButton = null;
            gamePadControl.getController().removeListener(controllerListener);
        }
    }

    @LmlAction("changeController")
    public void changeController(final VisSelectBox<String> select) {
        if (select.getSelectedIndex() < 0) {
            return;
        }
        final Controller controller = controllers.get(select.getSelectedIndex());
        ((GamePadControl) control).setController(controller);
        control.attachInputListener(null);
    }

    @LmlAction("invertX")
    public void setInvertX(final Button button) {
        ((GamePadControl) control).setInvertX(button.isChecked());
    }

    @LmlAction("invertY")
    public void setInvertY(final Button button) {
        ((GamePadControl) control).setInvertY(button.isChecked());
    }

    @LmlAction("invertXY")
    public void setInvertXY(final Button button) {
        ((GamePadControl) control).setInvertXY(button.isChecked());
    }

    /** Updates position of mock up entity. */
    private class MockUpdateAction extends Action {
        private final FloatRange x = new FloatRange(0f, 0.2f); // 0.2 is transition length (smoothness).
        private final FloatRange y = new FloatRange(0f, 0.2f);
        private float parentSize;
        private float size;
        private final Vector2 position = new Vector2();

        @Override
        public void reset() {
            parentSize = ((Layout) mockUpEntity.getParent()).getPrefWidth();
            size = mockUpEntity.getWidth();
            x.setCurrentValue(getX() * (parentSize - size));
            y.setCurrentValue(getY() * (parentSize - size));
            act(0f);
        }

        @Override
        public boolean act(final float delta) {
            x.setTargetValue(getX() * (parentSize - size));
            y.setTargetValue(getY() * (parentSize - size));
            x.update(delta);
            y.update(delta);
            position.set(mockUpEntity.getParent().getX() + (parentSize - size) / 2f,
                    mockUpEntity.getParent().getY() + (parentSize - size) / 2f);
            mockUpEntity.getParent().localToStageCoordinates(position);
            control.update(stage.getViewport(), position.x, position.y);
            mockUpEntity.setPosition(x.getCurrentValue(), y.getCurrentValue());
            return false;
        }

        // X and Y are in range of [-1, 1] - converting to [0, 1].
        private float getX() {
            return (control.getMovementDirection().x + 1f) / 2f;
        }

        private float getY() {
            return (control.getMovementDirection().y + 1f) / 2f;
        }
    }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.controller.dialog",
        fileName = "ControlsSwitchController.java",
        content = """package ${project.basic.rootPackage}.controller.dialog;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewDialogShower;
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;
import ${project.basic.rootPackage}.service.ControlsService;
import ${project.basic.rootPackage}.service.controls.Control;
import ${project.basic.rootPackage}.service.controls.ControlType;
import ${project.basic.rootPackage}.service.controls.impl.GamePadControl;
import ${project.basic.rootPackage}.service.controls.impl.InactiveControl;
import ${project.basic.rootPackage}.service.controls.impl.KeyboardControl;
import ${project.basic.rootPackage}.service.controls.impl.TouchControl;

/** Allows to switch control types. */
@ViewDialog(id = "switch", value = "ui/templates/dialogs/switch.lml", cacheInstance = true)
public class ControlsSwitchController implements ActionContainer, ViewDialogShower {
    @Inject private ControlsService service;
    @Inject private InterfaceService interfaceService;

    @Inject private ControlsController controlsController;
    @Inject private ControlsEditController editController;

    @LmlActor("PAD") private Button gamePadControlButton;
    private int playerId;

    /** @param playerId this screen will be used to choose controls for this player. */
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }

    @Override
    public void doBeforeShow(final Window dialog) {
        gamePadControlButton.setDisabled(GdxArrays.isEmpty(Controllers.getControllers()));
    }

    @LmlAction("controls")
    public Iterable<ControlType> getControlTypes() {
        if (GdxUtilities.isRunningOnAndroid()) {
            // Keyboard controls on Android do not work well...
            return GdxArrays.newArray(ControlType.TOUCH, ControlType.PAD, ControlType.INACTIVE);
        } else if (GdxUtilities.isRunningOnIOS()) {
            // Controllers (pads) do not exactly work on iOS.
            return GdxArrays.newArray(ControlType.TOUCH, ControlType.INACTIVE);
        } // Desktop supports all controllers:
        return GdxArrays.newArray(ControlType.values());
    }

    @LmlAction("TOUCH")
    public void setTouchControls() {
        changeControls(new TouchControl());
    }

    @LmlAction("INACTIVE")
    public void setInactiveControls() {
        changeControls(new InactiveControl());
    }

    @LmlAction("KEYBOARD")
    public void setKeyboardControls() {
        changeControls(new KeyboardControl());
    }

    @LmlAction("PAD")
    public void setGamePadControls() {
        final Array<Controller> controllers = Controllers.getControllers();
        if (GdxArrays.isEmpty(controllers)) {
            changeControls(new InactiveControl());
        } else {
            changeControls(new GamePadControl(controllers.first()));
        }
    }

    private void changeControls(final Control control) {
        service.setControl(playerId, control);
        controlsController.refreshPlayerView(playerId, control);
        if (control.isActive()) {
            editController.setControl(control);
            interfaceService.showDialog(ControlsEditController.class);
        }
    }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.controller.dialog",
        fileName = "NotEnoughPlayersErrorController.java",
        content = """package ${project.basic.rootPackage}.controller.dialog;

import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;

/** Shown when there are no players with active controls. */
@ViewDialog(id = "inactive", value = "ui/templates/dialogs/inactive.lml", cacheInstance = true)
public class NotEnoughPlayersErrorController {
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.controller.dialog",
        fileName = "SettingsController.java",
        content = """package ${project.basic.rootPackage}.controller.dialog;

import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxSets;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.util.LmlUtilities;
import ${project.basic.rootPackage}.service.FullscreenService;

/** This is a settings dialog, which can be shown in any view by using "show:settings" LML action or - in Java code -
 * through InterfaceService.showDialog(Class) method. Thanks to the fact that it implements ActionContainer, its methods
 * will be available in the LML template. */
@ViewDialog(id = "settings", value = "ui/templates/dialogs/settings.lml", cacheInstance = true)
public class SettingsController implements ActionContainer {
    @Inject private FullscreenService fullscreenService;

    /** @return array of serialized display modes' names. */
    @LmlAction("displayModes")
    public Array<String> getDisplayModes() {
        final ObjectSet<String> alreadyAdded = GdxSets.newSet(); // Removes duplicates.
        final Array<String> displayModes = GdxArrays.newArray(); // Keeps display modes sorted.
        for (final DisplayMode mode : fullscreenService.getDisplayModes()) {
            final String modeName = fullscreenService.serialize(mode);
            if (alreadyAdded.contains(modeName)) {
                continue; // Same size already added.
            }
            displayModes.add(modeName);
            alreadyAdded.add(modeName);
        }
        return displayModes;
    }

    /** @param actor its ID must match name of a display mode. */
    @LmlAction("setFullscreen")
    public void setFullscreenMode(final Actor actor) {
        final String modeName = LmlUtilities.getActorId(actor);
        final DisplayMode mode = fullscreenService.deserialize(modeName);
        fullscreenService.setFullscreen(mode);
    }

    /** Attempts to return to the original window size. */
    @LmlAction("resetFullscreen")
    public void setWindowedMode() {
        fullscreenService.resetFullscreen();
    }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.entity",
        fileName = "Player.java",
        content = """package ${project.basic.rootPackage}.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.viewport.Viewport;
import ${project.basic.rootPackage}.service.controls.Control;
import ${project.basic.rootPackage}.service.controls.ControlListener;

/** Represents a single player. */
public class Player implements ControlListener {
    private static final float DELAY = 0.75f; // Jump delay in seconds.
    private static final float SPEED = 750f; // Movement force. Affected by delta time.
    private static final float JUMP = 1000f; // Jump force.

    private final Control control;
    private final Body body;
    private final Viewport viewport;
    private boolean jumped;
    private float timeSinceLastJump = DELAY;

    public Player(final Control control, final Body body, final Viewport viewport) {
        this.control = control;
        this.body = body;
        this.viewport = viewport;
        control.setControlListener(this);
    }

    /** @return controls object that listens to player input. */
    public Control getControl() {
        return control;
    }

    /** @return Box2D body representing the player. */
    public Body getBody() {
        return body;
    }

    /** @param delta time since last update. */
    public void update(final float delta) {
        control.update(viewport, body.getPosition().x, body.getPosition().y);
        timeSinceLastJump += delta;
        final Vector2 movement = control.getMovementDirection();
        if (jumped && timeSinceLastJump > DELAY) {
            timeSinceLastJump = 0f;
            if (movement.x == 0f && movement.y == 0f) {
                body.applyForceToCenter(0f, JUMP, true);
            } else {
                body.applyForceToCenter(movement.x * JUMP, movement.y * JUMP, true);
            }
        }
        body.setActive(true);
        body.applyForceToCenter(movement.x * SPEED * delta, movement.y * SPEED * delta, true);
        jumped = false;
    }

    @Override
    public void jump() {
        jumped = true;
    }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.service",
        fileName = "Box2DService.java",
        content = """package ${project.basic.rootPackage}.service;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import ${project.basic.rootPackage}.${project.basic.mainClass};
import ${project.basic.rootPackage}.configuration.Configuration;
import ${project.basic.rootPackage}.entity.Player;
import ${project.basic.rootPackage}.service.controls.Control;

/** Manages 2D physics engine. */
@Component
public class Box2DService {
    private static final Vector2 GRAVITY = new Vector2(0f, -9.81f); // Box2D world gravity vector.
    private static final float STEP = 1f / 30f; // Length of a single Box2D step.
    private static final float WIDTH = ${project.basic.mainClass}.WIDTH / 10f; // Width of Box2D world.
    private static final float HEIGHT = ${project.basic.mainClass}.HEIGHT / 10f; // Height of Box2D world.
    private static final float SIZE = 3f; // Size of players.
    @Inject private ControlsService controlsService;

    private World world;
    private float timeSinceUpdate;
    private final Viewport viewport = new StretchViewport(WIDTH, HEIGHT);
    private final Array<Player> players = GdxArrays.newArray();

    /** Call this method to (re)create Box2D world according to current settings. */
    public void create() {
        dispose();
        world = new World(GRAVITY, true);
        createWorldBounds();
        final Array<Control> controls = controlsService.getControls();
        for (int index = 0; index < Configuration.PLAYERS_AMOUNT; index++) {
            final Control control = controls.get(index);
            if (control.isActive()) {
                players.add(new Player(control, getPlayerBody(index), viewport));
            }
        }
    }

    /** Creates Box2D bounds. */
    private void createWorldBounds() {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;

        final ChainShape shape = new ChainShape();
        shape.createLoop(new float[] { -WIDTH / 2f, -HEIGHT / 2f + SIZE * 2f, -WIDTH / 2f, HEIGHT / 2f, WIDTH / 2f,
                HEIGHT / 2f, WIDTH / 2f, -HEIGHT / 2f + SIZE * 2f });

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        world.createBody(bodyDef).createFixture(fixtureDef);
        shape.dispose();
    }

    /** @param index ID of the player. Affects body size and position.
     * @return a new player body. */
    private Body getPlayerBody(final int index) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.fixedRotation = false;
        final PolygonShape shape = new PolygonShape();
        switch (index) {
            case 0:
                bodyDef.position.set(-WIDTH / 2f + SIZE * 2f, HEIGHT / 4f);
                // Square.
                shape.setAsBox(SIZE, SIZE);
                break;
            case 1:
                bodyDef.position.set(0f, HEIGHT / 4f);
                // Hexagon. Ish.
                shape.set(new float[] { -SIZE, 0f, -SIZE / 2f, SIZE, SIZE / 2f, SIZE, SIZE, 0f, SIZE / 2f, -SIZE,
                        -SIZE / 2f, -SIZE, -SIZE, 0f });
                break;
            default:
                bodyDef.position.set(WIDTH / 2f - SIZE * 2f, HEIGHT / 4f);
                // Triangle.
                shape.set(new float[] { -SIZE, -SIZE, 0f, SIZE, SIZE, -SIZE, -SIZE, -SIZE });
        }
        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.3f;
        fixtureDef.density = 0.05f;
        final Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }

    /** @param delta time passed since last update. Will be used to update Box2D world. */
    public void update(final float delta) {
        timeSinceUpdate += delta;
        while (timeSinceUpdate > STEP) {
            timeSinceUpdate -= STEP;
            world.step(STEP, 8, 3);
            for (final Player player : players) {
                player.update(STEP);
            }
        }
    }

    /** @param inputMultiplexer will listen to player input events. */
    public void initiateControls(final InputMultiplexer inputMultiplexer) {
        for (final Player player : players) {
            player.getControl().attachInputListener(inputMultiplexer);
        }
    }

    /** @param width new screen width.
     * @param height new screen height. */
    public void resize(final int width, final int height) {
        viewport.update(width, height);
    }

    /** @return direct reference to current Box2D world. Might be null. */
    public World getWorld() {
        return world;
    }

    /** @return viewport with game coordinates. */
    public Viewport getViewport() {
        return viewport;
    }

    @Destroy
    public void dispose() {
        players.clear();
        if (world != null) {
            world.dispose();
            world = null;
        }
    }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.service",
        fileName = "ControlsService.java",
        content = """package ${project.basic.rootPackage}.service;

import com.badlogic.gdx.utils.Array;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.config.AutumnActionPriority;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import ${project.basic.rootPackage}.configuration.Configuration;
import ${project.basic.rootPackage}.configuration.preferences.ControlsData;
import ${project.basic.rootPackage}.configuration.preferences.ControlsPreference;
import ${project.basic.rootPackage}.service.controls.Control;

/** Manages players' controls. */
@Component
public class ControlsService {
    @Inject private ControlsPreference preference;
    private final Array<Control> controls = new Array<Control>();

    @Initiate
    public void readControlsFromPreferences() {
        final Array<ControlsData> controlsPreferences = preference.get();
        for (final ControlsData data : controlsPreferences) {
            controls.add(data.type.create(data));
        }
    }

    @Destroy(priority = AutumnActionPriority.TOP_PRIORITY)
    public void saveControlsInPreferences() {
        final Array<ControlsData> controlsData = GdxArrays.newArray(Configuration.PLAYERS_AMOUNT);
        for (final Control control : controls) {
            controlsData.add(control.toData());
        }
        controlsData.size = Configuration.PLAYERS_AMOUNT;
        preference.set(controlsData);
    }

    /** @param playerId ID of the player to check.
     * @return true if the player ID is valid and the player has an active controller attached. */
    public boolean isActive(final int playerId) {
        return GdxArrays.isIndexValid(controls, playerId) && controls.get(playerId).isActive();
    }

    /** @param playerId ID of the player.
     * @return controller assigned to the player. */
    public Control getControl(final int playerId) {
        return controls.get(playerId);
    }

    /** @param playerId ID of the player.
     * @param control will be assigned to the selected player. */
    public void setControl(final int playerId, final Control control) {
        controls.set(playerId, control);
    }

    /** @return controllers assigned to all players. Order matches players' IDs. */
    public Array<Control> getControls() {
        return controls;
    }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.service",
        fileName = "FullscreenService.java",
        content = """package ${project.basic.rootPackage}.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.kiwi.util.common.Strings;
import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Handles fullscreen-related operations. */
@Component
public class FullscreenService {
    /** @return supported fullscreen display modes. Utility method. */
    public DisplayMode[] getDisplayModes() {
        return Gdx.graphics.getDisplayModes();
    }

    /** @param displayMode will be converted to string.
     * @return passed mode converted to a string. */
    public String serialize(final DisplayMode displayMode) {
        return displayMode.width + "x" + displayMode.height;
    }

    /** @param displayMode serialized display mode. See {@link #serialize(DisplayMode)}.
     * @return mode instance or null with selected size is not supported. */
    public DisplayMode deserialize(final String displayMode) {
        final String[] sizes = Strings.split(displayMode, 'x');
        final int width = Integer.parseInt(sizes[0]);
        final int height = Integer.parseInt(sizes[1]);
        for (final DisplayMode mode : Gdx.graphics.getDisplayModes()) {
            if (mode.width == width && mode.height == height) {
                return mode;
            }
        }
        return null;
    }

    /** @param displayMode must support fullscreen mode. */
    public void setFullscreen(final DisplayMode displayMode) {
        if (Gdx.graphics.setFullscreenMode(displayMode)) {
            // Explicitly trying to resize the application listener to fully support all platforms:
            Gdx.app.getApplicationListener().resize(displayMode.width, displayMode.height);
        }
    }

    /** Tries to set windowed mode with initial screen size. */
    public void resetFullscreen() {
        if (Gdx.graphics.setWindowedMode(${project.basic.mainClass}.WIDTH, ${project.basic.mainClass}.HEIGHT)) {
            // Explicitly trying to resize the application listener to fully support all platforms:
            Gdx.app.getApplicationListener().resize(${project.basic.mainClass}.WIDTH, ${project.basic.mainClass}.HEIGHT);
        }
    }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.service.controls",
        fileName = "AbstractButtonControl.java",
        content = """package ${project.basic.rootPackage}.service.controls;

import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.viewport.Viewport;
import ${project.basic.rootPackage}.configuration.preferences.ControlsData;

/** Abstract base for controls that use buttons, like keyboard keys or game pads buttons. */
public abstract class AbstractButtonControl extends AbstractControl {
    protected IntSet pressedButtons = new IntSet(4);

    protected int up;
    protected int down;
    protected int left;
    protected int right;
    protected int jump;

    /** Updates current movement according to button states. */
    protected void updateMovement() {
        if (pressedButtons.size == 0) {
            stop();
        } else if (isPressed(up)) {
            if (isPressed(left)) { // Up-left.
                movement.set(-COS, SIN);
            } else if (isPressed(right)) { // Up-right.
                movement.set(COS, SIN);
            } else { // Up.
                movement.set(0f, 1f);
            }
        } else if (isPressed(down)) {
            if (isPressed(left)) { // Down-left.
                movement.set(-COS, -SIN);
            } else if (isPressed(right)) { // Down-right.
                movement.set(COS, -SIN);
            } else { // Down.
                movement.set(0f, -1f);
            }
        } else if (isPressed(left)) { // Left.
            movement.set(-1f, 0f);
        } else if (isPressed(right)) { // Right.
            movement.set(1f, 0f);
        } else {
            stop();
        }
    }

    @Override
    public void update(final Viewport gameViewport, final float gameX, final float gameY) {
        // Button controls usually do not need relative position of controlled entity.
    }

    /** @param key button code.
     * @return true if button is currently pressed. */
    protected boolean isPressed(final int key) {
        return pressedButtons.contains(key);
    }

    @Override
    public ControlsData toData() {
        final ControlsData data = new ControlsData(getType());
        data.up = up;
        data.down = down;
        data.left = left;
        data.right = right;
        data.jump = jump;
        return data;
    }

    @Override
    public void copy(final ControlsData data) {
        up = data.up;
        down = data.down;
        left = data.left;
        right = data.right;
        jump = data.jump;
    }

    @Override
    public void reset() {
        super.reset();
        pressedButtons.clear();
    }

    /** @return up movement button code. */
    public int getUp() {
        return up;
    }

    /** @param up will become up movement button code. */
    public void setUp(final int up) {
        pressedButtons.remove(this.up);
        updateMovement();
        this.up = up;
    }

    /** @return down movement button code. */
    public int getDown() {
        return down;
    }

    /** @param down will become down movement button code. */
    public void setDown(final int down) {
        pressedButtons.remove(this.down);
        updateMovement();
        this.down = down;
    }

    /** @return left movement button code. */
    public int getLeft() {
        return left;
    }

    /** @param left will become left movement button code. */
    public void setLeft(final int left) {
        pressedButtons.remove(this.left);
        updateMovement();
        this.left = left;
    }

    /** @return right movement button code. */
    public int getRight() {
        return right;
    }

    /** @param right will become right movement button code. */
    public void setRight(final int right) {
        pressedButtons.remove(this.right);
        updateMovement();
        this.right = right;
    }

    /** @return jump button code. */
    public int getJump() {
        return jump;
    }

    /** @param jump will become jump button code. */
    public void setJump(final int jump) {
        this.jump = jump;
    }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.service.controls",
        fileName = "AbstractControl.java",
        content = """package ${project.basic.rootPackage}.service.controls;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/** Abstract base for all controls. */
public abstract class AbstractControl implements Control {
    /** Sin value at NE corner. */
    protected static final float SIN = MathUtils.sin(MathUtils.atan2(1f, 1f));
    /** Cos value at NE corner. */
    protected static final float COS = MathUtils.cos(MathUtils.atan2(1f, 1f));

    private ControlListener listener;
    protected Vector2 movement = new Vector2();

    @Override
    public Vector2 getMovementDirection() {
        return movement;
    }

    @Override
    public void setControlListener(final ControlListener listener) {
        this.listener = listener;
    }

    /** @return should be notified about game events. */
    protected ControlListener getListener() {
        return listener;
    }

    /** @param angle in radians. */
    protected void updateMovementWithAngle(final float angle) {
        movement.x = MathUtils.cos(angle);
        movement.y = MathUtils.sin(angle);
    }

    /** Stops movement. */
    protected void stop() {
        movement.set(0f, 0f);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void reset() {
        movement.set(0f, 0f);
    }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.service.controls",
        fileName = "Control.java",
        content = """package ${project.basic.rootPackage}.service.controls;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import ${project.basic.rootPackage}.configuration.preferences.ControlsData;

/** Represents player entity controls. */
public interface Control {
    /** @param inputMultiplexer can be used to attach an input processor. */
    void attachInputListener(InputMultiplexer inputMultiplexer);

    /** @param gameViewport current state of game viewport. Might be used to convert units.
     * @param gameX x position of controlled entity in game units.
     * @param gameY y position of controlled entity in game units. */
    void update(Viewport gameViewport, float gameX, float gameY);

    /** @return current movement direction. Values should add up to [-1, 1]. */
    Vector2 getMovementDirection();

    /** @param listener should receive game events. */
    void setControlListener(ControlListener listener);

    /** @return serialized controls values that can be saved and read from. */
    ControlsData toData();

    /** @param data saved controls values that should be read. */
    void copy(ControlsData data);

    /** @return true if the player is active and can play with these controls. */
    boolean isActive();

    /** @return type of controls. */
    ControlType getType();

    /** Clears state variables. */
    void reset();
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.service.controls",
        fileName = "ControlListener.java",
        content = """package ${project.basic.rootPackage}.service.controls;

/** Listens to game events. */
public interface ControlListener {
    /** Invoked when controller detects jump input event. */
    void jump();
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.service.controls",
        fileName = "ControlType.java",
        content = """package ${project.basic.rootPackage}.service.controls;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import ${project.basic.rootPackage}.configuration.preferences.ControlsData;
import ${project.basic.rootPackage}.service.controls.impl.GamePadControl;
import ${project.basic.rootPackage}.service.controls.impl.InactiveControl;
import ${project.basic.rootPackage}.service.controls.impl.KeyboardControl;
import ${project.basic.rootPackage}.service.controls.impl.TouchControl;

/** Represents all types of available input sources. */
public enum ControlType {
    TOUCH {
        @Override
        public Control create(final ControlsData data) {
            return new TouchControl();
        }
    },
    KEYBOARD {
        @Override
        public Control create(final ControlsData data) {
            final Control control = new KeyboardControl();
            control.copy(data);
            return control;
        }
    },
    PAD {
        @Override
        public Control create(final ControlsData data) {
            final Array<Controller> controllers = Controllers.getControllers();
            if (GdxArrays.isEmpty(controllers) || !GdxArrays.isIndexValid(controllers, data.index)) {
                // Controller unavailable. Fallback to inactive.
                return new InactiveControl();
            }
            final Control control = new GamePadControl(controllers.get(data.index));
            control.copy(data);
            return control;
        }
    },
    INACTIVE {
        @Override
        public Control create(final ControlsData data) {
            return new InactiveControl();
        }
    };

    /** @param data serialized controls.
     * @return deserialized controller. */
    public abstract Control create(ControlsData data);
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.service.controls.impl",
        fileName = "GamePadControl.java",
        content = """package ${project.basic.rootPackage}.service.controls.impl;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.math.MathUtils;
import ${project.basic.rootPackage}.configuration.preferences.ControlsData;
import ${project.basic.rootPackage}.service.controls.AbstractButtonControl;
import ${project.basic.rootPackage}.service.controls.ControlType;

/** Allows to control an entity with controller events. */
public class GamePadControl extends AbstractButtonControl {
    private static final float DEADZONE = 0.2f;
    /** Axis alignment. */
    protected static final int X_LEFT = 0, X_RIGHT = 3, Y_LEFT = 1, Y_RIGHT = 2;
    /** Left becomes right. */
    private boolean invertX;
    /** Up becomes down. */
    private boolean invertY;
    /** Left becomes up. */
    private boolean invertXY;

    protected float axisX;
    protected float axisY;
    private Controller controller;
    private int controllerIndex;
    private final ControllerListener controllerListener = new ControllerAdapter() {
        @Override
        public boolean axisMoved(final Controller controller, final int axisIndex, final float value) {
            if (isAssignedTo(controller)) {
                updateAxisValue(axisIndex, value);
                return true;
            }
            return false;
        }

        @Override
        public boolean buttonDown(final Controller controller, final int buttonIndex) {
            if (isAssignedTo(controller)) {
                if (buttonIndex == up || buttonIndex == down || buttonIndex == left || buttonIndex == right) {
                    pressedButtons.add(buttonIndex);
                    updateMovement();
                    return true;
                } else if (buttonIndex == jump) {
                    getListener().jump();
                    return true;
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean buttonUp(final Controller controller, final int buttonIndex) {
            if (isAssignedTo(controller)) {
                if (buttonIndex == up || buttonIndex == down || buttonIndex == left || buttonIndex == right) {
                    pressedButtons.remove(buttonIndex);
                    updateMovement();
                    return true;
                }
                return true;
            }
            return false;
        }

// This might be needed if using an older gdx-controllers version. The povMoved() method is not present in
// the current version of gdx-controllers, and there isn't an immediately-clear replacement.
//        @Override
//        public boolean povMoved(final Controller controller, final int povIndex, final PovDirection direction) {
//            if (isAssignedTo(controller)) {
//                if (direction != null) {
//                    if (direction == PovDirection.center) {
//                        stop();
//                    } else {
//                        movement.x = getX(direction);
//                        movement.y = getY(direction);
//                    }
//                }
//                return true;
//            }
//            return false;
//        }
    };

    public GamePadControl() {
        up = 0;
        down = 2;
        left = 3;
        right = 1;
        jump = 4;
    }

    /** @param controller will be used to control the entity. */
    public GamePadControl(final Controller controller) {
        this();
        this.controller = controller;
    }

    /** @return the device that this input processor is assigned to. */
    public Controller getController() {
        return controller;
    }

    /** @param controller will be used to control the entity. */
    public void setController(final Controller controller) {
        if (this.controller != null) {
            this.controller.removeListener(controllerListener);
        }
        this.controller = controller;
        if (controller != null) {
            controllerIndex = Controllers.getControllers().indexOf(controller, true);
        }
    }

    /** @param controller a {@link Controller} instance. Can be null.
     * @return true if this input processor is assigned to passed controller. */
    public boolean isAssignedTo(final Controller controller) {
        return this.controller.equals(controller);
    }

    protected void updateAxisValue(final int axisIndex, float value) {
        if (isY(axisIndex)) { // Inverting Y coordinates.
            value = -value;
        }
        if (!invertXY && isX(axisIndex) || invertXY && isY(axisIndex)) {
            if (value > DEADZONE || value < -DEADZONE) {
                axisX = invertX ? -value : value;
            } else {
                axisX = 0f;
            }
        } else {
            if (value > DEADZONE || value < -DEADZONE) {
                axisY = invertY ? -value : value;
            } else {
                axisY = 0f;
            }
        }
        if (Float.compare(axisX, 0f) == 0 && Float.compare(axisY, 0f) == 0) {
            stop();
        } else {
            updateMovementWithAngle(MathUtils.atan2(axisY, axisX));
        }

    }

// This might be needed if using an older gdx-controllers version. PovDirection is not present in
// the current version of gdx-controllers, and this code is only used by povMoved(), which is also
// commented out.
//    protected float getX(final PovDirection direction) {
//        final float x;
//        if (invertXY) { // Checking Y axis (north=east, south=west):
//            x = getAbsoluteY(direction);
//        } else { // Checking X axis:
//            x = getAbsoluteX(direction);
//        }
//        if (invertX) {
//            return -x;
//        }
//        return x;
//    }
//
//    protected float getY(final PovDirection direction) {
//        final float y;
//        if (invertXY) { // Checking X axis (north=east, south=west):
//            y = getAbsoluteX(direction);
//        } else { // Checking Y axis:
//            y = getAbsoluteY(direction);
//        }
//        if (invertY) {
//            return -y;
//        }
//        return y;
//    }
//
//    protected float getAbsoluteX(final PovDirection direction) {
//        if (direction == PovDirection.east) {
//            return 1f;
//        } else if (direction == PovDirection.northEast || direction == PovDirection.southEast) {
//            return COS;
//        } else if (direction == PovDirection.west) {
//            return -1f;
//        } else if (direction == PovDirection.northWest || direction == PovDirection.southWest) {
//            return -COS;
//        }
//        return 0f;
//    }
//
//    protected float getAbsoluteY(final PovDirection direction) {
//        if (direction == PovDirection.north) {
//            return 1f;
//        } else if (direction == PovDirection.northEast || direction == PovDirection.northWest) {
//            return SIN;
//        } else if (direction == PovDirection.south) {
//            return -1f;
//        } else if (direction == PovDirection.southWest || direction == PovDirection.southEast) {
//            return -SIN;
//        } else {
//            return 0f;
//        }
//    }

    private static boolean isX(final int axisIndex) {
        return axisIndex == X_LEFT || axisIndex == X_RIGHT;
    }

    private static boolean isY(final int axisIndex) {
        return axisIndex == Y_LEFT || axisIndex == Y_RIGHT;
    }

    protected float getAxisAngle() {
        return MathUtils.atan2(axisY, axisX) * MathUtils.radiansToDegrees;
    }

    /** @return true if X movement is inverted. */
    public boolean isInvertX() {
        return invertX;
    }

    /** @param invertX true to invert X movement. */
    public void setInvertX(final boolean invertX) {
        this.invertX = invertX;
    }

    /** @return true if Y movement is inverted. */
    public boolean isInvertY() {
        return invertY;
    }

    /** @param invertY true to invert Y movement. */
    public void setInvertY(final boolean invertY) {
        this.invertY = invertY;
    }

    /** @return true if X and Y movement are inverted with each other. */
    public boolean isInvertXY() {
        return invertXY;
    }

    /** @param invertXY true to invert X and Y movement with each other. */
    public void setInvertXY(final boolean invertXY) {
        this.invertXY = invertXY;
    }

    @Override
    public void attachInputListener(final InputMultiplexer inputMultiplexer) {
        controller.removeListener(controllerListener); // Making sure listener is not added twice.
        controller.addListener(controllerListener);
    }

    @Override
    public ControlsData toData() {
        final ControlsData data = super.toData();
        data.invertX = invertX;
        data.invertY = invertY;
        data.invertXY = invertXY;
        data.index = controllerIndex;
        return data;
    }

    @Override
    public void copy(final ControlsData data) {
        super.copy(data);
        invertX = data.invertX;
        invertY = data.invertY;
        invertXY = data.invertXY;
    }

    @Override
    public ControlType getType() {
        return ControlType.PAD;
    }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.service.controls.impl",
        fileName = "InactiveControl.java",
        content = """package ${project.basic.rootPackage}.service.controls.impl;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import ${project.basic.rootPackage}.configuration.preferences.ControlsData;
import ${project.basic.rootPackage}.service.controls.Control;
import ${project.basic.rootPackage}.service.controls.ControlListener;
import ${project.basic.rootPackage}.service.controls.ControlType;

/** Mock-up controls representing an inactive player. */
public class InactiveControl implements Control {
    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public ControlType getType() {
        return ControlType.INACTIVE;
    }

    @Override
    public void attachInputListener(final InputMultiplexer inputMultiplexer) {
    }

    @Override
    public void update(final Viewport gameViewport, final float gameX, final float gameY) {
    }

    @Override
    public Vector2 getMovementDirection() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setControlListener(final ControlListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ControlsData toData() {
        return new ControlsData(getType());
    }

    @Override
    public void copy(final ControlsData data) {
    }

    @Override
    public void reset() {
    }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.service.controls.impl",
        fileName = "KeyboardControl.java",
        content = """package ${project.basic.rootPackage}.service.controls.impl;

import com.badlogic.gdx.Input.Keys;
import ${project.basic.rootPackage}.service.controls.AbstractButtonControl;
import ${project.basic.rootPackage}.service.controls.ControlType;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;

/** Allows to control an entity with keyboard events. */
public class KeyboardControl extends AbstractButtonControl {
    public KeyboardControl() {
        // Initial settings:
        up = Keys.UP;
        down = Keys.DOWN;
        left = Keys.LEFT;
        right = Keys.RIGHT;
        jump = Keys.SPACE;
    }

    @Override
    public void attachInputListener(final InputMultiplexer inputMultiplexer) {
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(final int keycode) {
                if (keycode == up || keycode == down || keycode == left || keycode == right) {
                    pressedButtons.add(keycode);
                    updateMovement();
                    return true;
                } else if (keycode == jump) {
                    getListener().jump();
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyUp(final int keycode) {
                if (keycode == up || keycode == down || keycode == left || keycode == right) {
                    pressedButtons.remove(keycode);
                    updateMovement();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public ControlType getType() {
        return ControlType.KEYBOARD;
    }
}"""
      )
    )

    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = "${project.basic.rootPackage}.service.controls.impl",
        fileName = "TouchControl.java",
        content = """package ${project.basic.rootPackage}.service.controls.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import ${project.basic.rootPackage}.configuration.preferences.ControlsData;
import ${project.basic.rootPackage}.service.controls.AbstractControl;
import ${project.basic.rootPackage}.service.controls.ControlType;

/** Allows to control entity with touch events. */
public class TouchControl extends AbstractControl {
    private final Vector2 entityPosition = new Vector2();
    private boolean isMoving;
    private float x;
    private float y;

    @Override
    public void attachInputListener(final InputMultiplexer inputMultiplexer) {
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
                updateDirection(screenX, Gdx.graphics.getHeight() - screenY);
                isMoving = true;
                getListener().jump();
                return false;
            }

            @Override
            public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
                updateDirection(screenX, Gdx.graphics.getHeight() - screenY);
                return false;
            }

            @Override
            public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
                stop();
                isMoving = false;
                return false;
            }
        });
    }

    private void updateDirection(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void update(final Viewport gameViewport, final float gameX, final float gameY) {
        gameViewport.project(entityPosition.set(gameX, gameY));
        if (isMoving) {
            updateMovementWithAngle(MathUtils.atan2(y - entityPosition.y, x - entityPosition.x));
        }
    }

    @Override
    public ControlsData toData() {
        return new ControlsData(getType()); // Touch controls require no shortcuts.
    }

    @Override
    public void copy(final ControlsData data) {
        // Touch controls require no shortcuts.
    }

    @Override
    public ControlType getType() {
        return ControlType.TOUCH;
    }
}"""
      )
    )
  }

  override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

/** This class serves only as the application scanning root. Any classes in its package (or any of the sub-packages)
 * with proper Autumn MVC annotations will be found, scanned and initiated. */
public class ${project.basic.mainClass} {
    /** Default application size. */
    public static final int WIDTH = 450, HEIGHT = 600;
}"""
}
