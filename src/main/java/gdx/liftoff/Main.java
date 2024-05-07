package gdx.liftoff;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.ray3k.stripe.*;
import gdx.liftoff.ui.OverlayTable;
import gdx.liftoff.ui.RootTable;
import gdx.liftoff.ui.data.UserData;
import org.lwjgl.PointerBuffer;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;

//todo:Fix openal error
//todo:add preference to start in fullscreen
/**
 * Main launcher of the app. Contains utility methods and object instances for use throughout the program
 */
public class Main extends ApplicationAdapter {
    public static Skin skin;
    public static Stage stage;
    public static ScreenViewport screenViewport;
    public static FitViewport fitViewport;
    public static SpriteBatch batch;
    public static RootTable root;
    public static OverlayTable overlayTable;
    public static SystemCursorListener handListener;
    public static SystemCursorListener ibeamListener;
    public static ScrollFocusListener scrollFocusListener;
    public static Color CLEAR_WHITE = new Color(1, 1, 1, 0);
    public static Image bgImage = new Image();
    public static boolean resizingWindow;
    public static Properties prop;
    public static Preferences pref;
    private static GlyphLayout layout = new GlyphLayout();
    public static final int MIN_WINDOW_WIDTH = 400;
    public static final int MIN_WINDOW_HEIGHT = 410;
    public static final float ROOT_TABLE_PREF_WIDTH = 600;
    public static final float ROOT_TABLE_PREF_HEIGHT = 700;

    public static final float SPACE_SMALL = 5;
    public static final float SPACE_MEDIUM = 10;
    public static final float SPACE_LARGE = 20;
    public static final float SPACE_HUGE = 30;
    public static final float TOOLTIP_WIDTH = 200;
    public static final float TOOLTIP_WIDTH_LARGE = 300;

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("GDX-Liftoff");
        config.useVsync(true);
        config.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
        config.setWindowedMode(800, 800);
        config.setWindowSizeLimits(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT, -1, -1);
        config.setWindowIcon("icons/libgdx128.png", "icons/libgdx64.png", "icons/libgdx32.png", "icons/libgdx16.png");
        new Lwjgl3Application(new Main(), config);
    }

    @Override
    public void create() {
        prop = new Properties();
        try {
            prop.load(Gdx.files.internal("ui-data/nls.properties").read());
            prop.load(Gdx.files.internal("ui-data/urls.properties").read());
            prop.load(Gdx.files.internal("ui-data/defaults.properties").read());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        pref = Gdx.app.getPreferences("gdx-liftoff-prefs");

        setDefaultUserData();

        skin = new Skin(Gdx.files.internal("ui-skin/skin.json"));

        fitViewport = new FitViewport(1920, 1080);
        screenViewport = new ScreenViewport();
        batch = new SpriteBatch();
        stage = new Stage(screenViewport, batch);


        Gdx.input.setInputProcessor(stage);

        handListener = new SystemCursorListener(SystemCursor.Hand);
        ibeamListener = new SystemCursorListener(SystemCursor.Ibeam);
        scrollFocusListener = new ScrollFocusListener(stage);

        bgImage = new Image(skin, "bg");
        bgImage.setFillParent(true);
        bgImage.setScaling(Scaling.fill);
        stage.addActor(bgImage);

        root = new RootTable();
        root.setFillParent(true);
        stage.addActor(root);

        overlayTable = new OverlayTable();
        overlayTable.setFillParent(true);
        stage.addActor(overlayTable);
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);

        //draw stage
        stage.getViewport().apply();
        stage.act();
        stage.draw();

        resizingWindow = false;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        resizingWindow = true;
    }

    public static void addHandListener(Actor actor) {
        actor.addListener(handListener);
    }

    public static void addIbeamListener(Actor actor) {
        actor.addListener(ibeamListener);
    }

    public static void addScrollFocusListener(Actor actor) {
        actor.addListener(scrollFocusListener);
    }

    public static void onChange(Actor actor, Runnable runnable) {
        actor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                runnable.run();
            }
        });
    }

    public static void onClick(Actor actor, Runnable runnable) {
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                runnable.run();
            }
        });
    }

    public static PopTable addTooltip(Actor actor, int align, String text) {
        return addTooltip(actor, null, align, 0, text);
    }

    public static PopTable addTooltip(Actor actor, int align, float wrapWidth, String text) {
        return addTooltip(actor, null, align, wrapWidth,  text);
    }

    public static PopTable addTooltip(Actor actor, Actor attachedActor, int align, float wrapWidth, String text) {
        String style = (align & Align.bottom) != 0 ? "tooltip-arrow-up" : (align & Align.top) != 0  ? "tooltip-arrow-down" : (align & Align.left) != 0  ? "tooltip-arrow-right" : "tooltip-arrow-left";
        PopTableTextHoverListener listener = new PopTableTextHoverListener(text, wrapWidth, align, align, skin, style);
        listener.attachedActor = attachedActor;
        actor.addListener(listener);
        return listener.getPopTable();
    }

    public static PopTable addPopTableClickListener(Actor actor, int align, String description) {
        return addPopTableClickListener(actor, null, align, 0, description);
    }

    public static PopTable addPopTableClickListener(Actor actor, int align, float wrapWidth, String description) {
        return addPopTableClickListener(actor, null, align, wrapWidth,  description);
    }

    /**
     * Utility method to attach a PopTable to an associated actor which will appear upon clicking the actor
     * @param actor The actor to be clicked
     * @param attachedActor The actor that the position of the PopTable will be relative to. This can differ from the
     *                      actor
     * @param align The alignment of the PopTable
     * @param wrapWidth Set to 0 to not enable wrapping of the Label
     * @param text The text to be added inside the PopTable
     * @return
     */
    public static PopTable addPopTableClickListener(Actor actor, Actor attachedActor, int align, float wrapWidth, String text) {
        String style = (align & Align.bottom) != 0 ? "tooltip-arrow-up" : (align & Align.top) != 0  ? "tooltip-arrow-down" : (align & Align.left) != 0  ? "tooltip-arrow-right" : "tooltip-arrow-left";
        PopTableClickListener listener = new PopTableClickListener(align, align, skin, style);
        listener.attachedActor = attachedActor;
        actor.addListener(listener);
        PopTable pop = listener.getPopTable();

        Label label = new Label(text, skin, "tooltip");
        Cell cell = pop.add(label);
        if (wrapWidth != 0) {
            cell.width(wrapWidth);
        } else {
            layout.setText(label.getStyle().font, text);
            cell.minWidth(0).prefWidth(layout.width);
        }

        return pop;
    }

    public static void addLabelHighlight(Actor actor, Label label) {
        addLabelHighlight(actor, label, true);
    }

    /**
     * Utility method to change the color of a label to make it look highlighted when the user enters another specified
     * actor
     * @param actor
     * @param label
     * @param changeColor
     */
    public static void addLabelHighlight(Actor actor, Label label, boolean changeColor) {
        label.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer == -1) {
                    if (changeColor) label.setColor(skin.getColor("red"));
                    actor.fire(event);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (pointer == -1) {
                    if (changeColor) label.setColor(Color.WHITE);
                    actor.fire(event);
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!(actor instanceof Button)) return;
                Button button = (Button) actor;
                button.setChecked(!button.isChecked());
            }
        });

        actor.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1 && changeColor) label.setColor(skin.getColor("red"));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1 && changeColor) label.setColor(skin.getColor("white"));
            }
        });
    }

    /**
     * Utility method to display a native file picker
     * @param initialFolder
     * @param callback
     */
    public static void pickDirectory(FileHandle initialFolder, FileChooserAdapter callback) {
        String initialPath = initialFolder.path();

        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")) {
            initialPath = initialPath.replace("/", "\\");
        }

        PointerBuffer pathPointer = memAllocPointer(1);

        try {
            int status = NativeFileDialog.NFD_PickFolder(initialPath, pathPointer);

            if (status == NativeFileDialog.NFD_CANCEL) {
                callback.canceled();
                return;
            }

            // Unexpected error - show VisUI dialog.
            if (status != NativeFileDialog.NFD_OKAY) {
                throw new Throwable("Native file dialog error");
            }

            String folder = pathPointer.getStringUTF8(0);
            NativeFileDialog.nNFD_Free(pathPointer.get(0));

            Array<FileHandle> array = new Array<>();
            array.add(Gdx.files.absolute(folder));

            callback.selected(array);
        } catch (Throwable e) {
            Gdx.app.error(
                "NFD",
                "The Native File Dialog library could not be loaded.\n" +
                    "Check if you have multiple LWJGL3 applications open simultaneously,\n" +
                    "since that can cause this error."
            );
            Gdx.app.error("NFD", e.toString());
            FileChooser fileChooser = new FileChooser(FileChooser.Mode.OPEN);
            fileChooser.setSelectionMode(SelectionMode.DIRECTORIES);
            fileChooser.setDirectory(initialPath);
            fileChooser.setListener(callback);

            stage.addActor(fileChooser.fadeIn());
        } finally {
            memFree(pathPointer);
        }
    }

    public static void maximizeWindow() {
        Lwjgl3Graphics g = (Lwjgl3Graphics) Gdx.graphics;
        g.getWindow().maximizeWindow();
    }

    public static void restoreWindow() {
        Lwjgl3Graphics g = (Lwjgl3Graphics) Gdx.graphics;
        g.getWindow().restoreWindow();
    }

    /**
     * Splits a comma separated value list in a String and returns an Array{@literal <}String{@literal >}
     *
     * @param string
     * @return
     */
    public static Array<String> splitCSV(String string) {
        return string.isEmpty() ? new Array<>() : new Array<>(string.split(","));
    }

    public static void setDefaultUserData() {
        UserData.projectName = pref.getString("Name", prop.getProperty("projectNameDefault"));
        UserData.packageName = pref.getString("Package", prop.getProperty("packageNameDefault"));
        UserData.mainClassName = pref.getString("MainClass", prop.getProperty("mainClassNameDefault"));
        UserData.platforms = splitCSV(prop.getProperty("platformsDefaultNames"));

        UserData.languages = splitCSV(prop.getProperty("languagesDefaultNames"));
        Array<String> languageVersions = splitCSV(prop.getProperty("languagesDefaultVersions"));
        UserData.languageVersions = new OrderedMap<>();
        for (int i = 0; i < UserData.languages.size; i++) {
            UserData.languageVersions.put(UserData.languages.get(i), languageVersions.get(i));
        }

        UserData.extensions = splitCSV(prop.getProperty("extensionsDefaultNames"));
        UserData.template = prop.getProperty("templateDefaultName");
        UserData.thirdPartyLibs = splitCSV(prop.getProperty("platformsDefaultNames"));
        UserData.libgdxVersion = prop.getProperty("libgdxDefaultVersion");
        UserData.javaVersion = prop.getProperty("javaDefaultVersion");
        UserData.appVersion = prop.getProperty("appDefaultVersion");
        UserData.robovmVersion = prop.getProperty("robovmDefaultVersion");
        UserData.addGuiAssets = Boolean.parseBoolean(prop.getProperty("addGuiAssetsDefault"));
        UserData.addReadme = Boolean.parseBoolean(prop.getProperty("addReadmeDefault"));
        UserData.gradleTasks = pref.getString("GradleTasks", prop.getProperty("gradleTasksDefault"));
        UserData.projectPath = prop.getProperty("projectPathDefault");
        UserData.androidPath = pref.getString("AndroidSdk", prop.getProperty("androidPathDefault"));
        UserData.log = prop.getProperty("generationEnd") + "\n" + prop.getProperty("generationEnd");
    }

    public static void setQuickProjectDefaultUserData() {
        UserData.platforms = splitCSV(prop.getProperty("qp_platformsDefaultNames"));

        UserData.languages = splitCSV(prop.getProperty("languagesDefaultNames"));
        Array<String> languageVersions = splitCSV(prop.getProperty("languagesDefaultVersions"));
        UserData.languageVersions = new OrderedMap<>();
        for (int i = 0; i < UserData.languages.size; i++) {
            UserData.languageVersions.put(UserData.languages.get(i), languageVersions.get(i));
        }

        UserData.extensions = splitCSV(prop.getProperty("extensionsDefaultNames"));
        UserData.template = prop.getProperty("templateDefaultName");
        UserData.thirdPartyLibs = splitCSV(prop.getProperty("platformsDefaultNames"));
        UserData.libgdxVersion = prop.getProperty("libgdxDefaultVersion");
        UserData.javaVersion = prop.getProperty("javaDefaultVersion");
        UserData.appVersion = prop.getProperty("appDefaultVersion");
        UserData.robovmVersion = prop.getProperty("robovmDefaultVersion");
        UserData.addGuiAssets = Boolean.parseBoolean(prop.getProperty("addGuiAssetsDefault"));
        UserData.addReadme = Boolean.parseBoolean(prop.getProperty("addReadmeDefault"));
        UserData.gradleTasks = prop.getProperty("gradleTasksDefault");
    }
}
