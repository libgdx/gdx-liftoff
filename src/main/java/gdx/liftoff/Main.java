package gdx.liftoff;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.ray3k.stripe.*;
import gdx.liftoff.data.platforms.Platform;
import gdx.liftoff.data.project.*;
import gdx.liftoff.ui.OverlayTable;
import gdx.liftoff.ui.RootTable;
import gdx.liftoff.ui.UserData;
import gdx.liftoff.ui.dialogs.FullscreenCompleteDialog;
import gdx.liftoff.ui.dialogs.FullscreenDialog;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.io.IOException;
import java.lang.StringBuilder;
import java.util.*;
import java.util.Collections;

import static gdx.liftoff.ui.UserData.*;
import static gdx.liftoff.ui.dialogs.FullscreenCompleteDialog.*;
import static gdx.liftoff.ui.dialogs.FullscreenDialog.fullscreenDialog;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;

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
    public static boolean generatingProject;
    public static String latestStableVersion;
    public static Properties prop;
    public static Preferences pref;
    private static final GlyphLayout layout = new GlyphLayout();
    public static final int MIN_WINDOW_WIDTH = 400;
    public static final int MIN_WINDOW_HEIGHT = 410;
    public static final int WINDOW_BORDER = 50;
    public static final float FULLSCREEN_MIN_WIDTH = 1500;
    public static final float FULLSCREEN_MIN_HEIGHT = 880;
    public static final float ROOT_TABLE_PREF_WIDTH = 600;
    public static final float ROOT_TABLE_PREF_HEIGHT = 700;

    public static final float SPACE_SMALL = 5;
    public static final float SPACE_MEDIUM = 10;
    public static final float SPACE_LARGE = 20;
    public static final float SPACE_HUGE = 30;
    public static final float TOOLTIP_WIDTH = 200;
    public static final float TOOLTIP_WIDTH_LARGE = 300;

    private static String exceptionToString(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : ex.getStackTrace()) {
            sb.append(ste.toString()).append('\n');
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired(true)) return; // This handles macOS support and helps on Windows.
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("GDX-Liftoff");
        config.disableAudio(true);
        config.useVsync(true);
        config.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
        config.setIdleFPS(8);

        DisplayMode primaryDesktopMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
        int monitorWidth = primaryDesktopMode.width;
        int monitorHeight=  primaryDesktopMode.height;
        int windowWidth = Math.max(MathUtils.round(monitorWidth / 1920f * 800f), 800);
        int windowHeight = Math.max(MathUtils.round(monitorHeight / 1080f * 800f), 800);
        config.setWindowedMode(Math.min(windowWidth, monitorWidth - WINDOW_BORDER * 2), Math.min(windowHeight, monitorHeight - WINDOW_BORDER * 2));

        config.setWindowSizeLimits(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT, -1, -1);
        config.setWindowIcon("icons/libgdx128.png", "icons/libgdx64.png", "icons/libgdx32.png", "icons/libgdx16.png");
        config.setAutoIconify(true);
        final Lwjgl3WindowListener windowListener = new Lwjgl3WindowListener() {
            @Override
            public void created(Lwjgl3Window lwjgl3Window) {

            }

            @Override
            public void iconified(boolean b) {

            }

            @Override
            public void maximized(boolean isMax) {
                if (isMax){
                    boolean fullscreenMode = Gdx.graphics.getWidth() > FULLSCREEN_MIN_WIDTH &&
                        Gdx.graphics.getHeight() > FULLSCREEN_MIN_HEIGHT;
                    if (fullscreenMode && root != null) {
                        Gdx.app.postRunnable(() -> {
                            root.getCurrentTable().finishAnimation();
                            if (root.getCurrentTable() == root.completeTable) FullscreenCompleteDialog.show(false);
                            else FullscreenDialog.show();
                            root.fadeOutTable();
                        });
                    }
                    if(fullscreenMode && overlayTable != null)
                        overlayTable.fadeOut();
                } else {
                    if (fullscreenDialog != null) {
                        fullscreenDialog.hide();
                        root.getCurrentTable().populate();
                        root.fadeInTable();
                        overlayTable.fadeIn();
                    } else if (fullscreenCompleteDialog != null) {
                        fullscreenCompleteDialog.hide();
                        if (root != null) {
                            root.fadeInTable();
                            overlayTable.fadeIn();

                            if (root.getCurrentTable() != root.completeTable) {
                                root.showTableInstantly(root.completeTable);
                                root.completeTable.showCompletePanel();
                            }
                        }
                    }
                }
                pref.putBoolean("startMaximized", isMax);
                pref.flush();

            }

            @Override
            public void focusLost() {
                Gdx.graphics.setContinuousRendering(false);
            }

            @Override
            public void focusGained() {
                Gdx.graphics.setContinuousRendering(true);
            }

            @Override
            public boolean closeRequested() {
                return true;
            }

            @Override
            public void filesDropped(String[] strings) {

            }

            @Override
            public void refreshRequested() {

            }
        };
        config.setWindowListener(windowListener);
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

        skin.getFont("font-label-tooltip").getData().breakChars = new char[]{'-'};

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

        checkSetupVersion();

        DisplayMode primaryDesktopMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
        int width = primaryDesktopMode.width;
        int height = primaryDesktopMode.height;
        if (!pref.contains("startMaximized") && width > 1920 && height > 1080 || pref.getBoolean("startMaximized", false)) {
            Main.maximizeWindow();
        }
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
        return addTooltip(actor, null, align, wrapWidth, text);
    }

    public static PopTable addTooltip(Actor actor, Actor attachedActor, int align, float wrapWidth, String text) {
        String style = (align & Align.bottom) != 0 ? "tooltip-arrow-up" : (align & Align.top) != 0 ? "tooltip-arrow-down" : (align & Align.left) != 0 ? "tooltip-arrow-right" : "tooltip-arrow-left";
        PopTableTextHoverListener listener = new PopTableTextHoverListener(text, wrapWidth, align, align, skin, style);
        listener.attachedActor = attachedActor;
        actor.addListener(listener);
        return listener.getPopTable();
    }

    public static PopTable addPopTableClickListener(Actor actor, int align, String description) {
        return addPopTableClickListener(actor, null, align, 0, description);
    }

    public static PopTable addPopTableClickListener(Actor actor, int align, float wrapWidth, String description) {
        return addPopTableClickListener(actor, null, align, wrapWidth, description);
    }

    /**
     * Utility method to attach a PopTable to an associated actor which will appear upon clicking the actor
     *
     * @param actor         The actor to be clicked
     * @param attachedActor The actor that the position of the PopTable will be relative to. This can differ from the
     *                      actor
     * @param align         The alignment of the PopTable
     * @param wrapWidth     Set to 0 to not enable wrapping of the Label
     * @param text          The text to be added inside the PopTable
     * @return The generated PopTable that is shown when the user clicks the actor
     */
    public static PopTable addPopTableClickListener(Actor actor, Actor attachedActor, int align, float wrapWidth, String text) {
        String style = "tooltip-arrow-left";
        if ((align & Align.bottom) != 0) style = "tooltip-arrow-up";
        else if ((align & Align.top) != 0) style = "tooltip-arrow-down";
        else if ((align & Align.left) != 0) style = "tooltip-arrow-right";
        PopTableClickListener listener = new PopTableClickListener(align, align, skin, style);
        listener.attachedActor = attachedActor;
        actor.addListener(listener);
        PopTable pop = listener.getPopTable();

        Label label = new Label(text, skin, "tooltip");
        Cell<Label> cell = pop.add(label);
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
     *
     * @param actor       The affected actor
     * @param label       The label to be highlighted
     * @param changeColor The color of the highlight
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
     *
     * @param initialFolder The initial folder that the picker will start in
     * @param callback      Adapter that will be called if the user clicks okay or cancels the dialog
     */
    public static void pickDirectory(FileHandle initialFolder, FileChooserAdapter callback) {
        String initialPath = initialFolder.path();

        if (UIUtils.isWindows) {
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
     * Splits a comma separated value list in a String and returns an ArrayList{@literal <}String{@literal >}.
     *
     * @param string The raw String of comma separated values
     * @return The list of String values
     */
    public static ArrayList<String> splitCSV(String string) {
        return string.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(string.split(",")));
    }

    /**
     * Splits a comma separated value list in a String and returns a LinkedHashSet{@literal <}String{@literal >}.
     *
     * @param string The raw String of comma separated values
     * @return The set of String values, in order
     */
    public static LinkedHashSet<String> splitCSVSet(String string) {
        return string.isEmpty() ? new LinkedHashSet<>() : new LinkedHashSet<>(Arrays.asList(string.split(",")));
    }

    public static void setDefaultUserData() {
        UserData.projectName = pref.getString("Name", prop.getProperty("projectNameDefault"));
        UserData.packageName = pref.getString("Package", prop.getProperty("packageNameDefault"));
        UserData.mainClassName = pref.getString("MainClass", prop.getProperty("mainClassNameDefault"));
        UserData.platforms = splitCSV(pref.getString("Platforms", prop.getProperty("platformsDefaultNames")));

        UserData.languages = splitCSV(pref.getString("Languages", prop.getProperty("languagesDefaultNames")));
        ArrayList<String> languageVersions = splitCSV(pref.getString("LanguageVersions", prop.getProperty("languagesDefaultVersions")));
        UserData.languageVersions = new LinkedHashMap<>();
        for (int i = 0; i < UserData.languages.size(); i++) {
            UserData.languageVersions.put(UserData.languages.get(i), languageVersions.get(i));
        }

        extensions = splitCSV(pref.getString("Extensions", prop.getProperty("extensionsDefaultNames")));
        UserData.template = prop.getProperty("templateDefaultName");
        UserData.thirdPartyLibs = splitCSVSet(pref.getString("ThirdParty", prop.getProperty("thirdPartyDefaultNames")));
        thirdPartyLibs.retainAll(Listing.unofficialNames);
        UserData.libgdxVersion = prop.getProperty("libgdxDefaultVersion");
        UserData.javaVersion = prop.getProperty("javaDefaultVersion");
        appVersion = prop.getProperty("appDefaultVersion");
        androidPluginVersion = prop.getProperty("androidPluginDefaultVersion");
        UserData.robovmVersion = prop.getProperty("robovmDefaultVersion");
        gwtPluginVersion = prop.getProperty("gwtPluginDefaultVersion");
        UserData.addGuiAssets = Boolean.parseBoolean(prop.getProperty("addGuiAssetsDefault"));
        UserData.addReadme = Boolean.parseBoolean(prop.getProperty("addReadmeDefault"));
        UserData.gradleTasks = pref.getString("GradleTasks", prop.getProperty("gradleTasksDefault"));
        UserData.projectPath = pref.getString("projectPath", prop.getProperty("projectPathDefault"));
        UserData.androidPath = pref.getString("AndroidSdk", prop.getProperty("androidPathDefault"));
        UserData.log = "";
    }

    public static void setQuickProjectDefaultUserData() {
        UserData.platforms = splitCSV(prop.getProperty("qp_platformsDefaultNames"));

        UserData.languages = splitCSV(prop.getProperty("languagesDefaultNames"));
        ArrayList<String> languageVersions = splitCSV(prop.getProperty("languagesDefaultVersions"));
        UserData.languageVersions = new LinkedHashMap<>();
        for (int i = 0; i < UserData.languages.size(); i++) {
            UserData.languageVersions.put(UserData.languages.get(i), languageVersions.get(i));
        }

        extensions = splitCSV(prop.getProperty("extensionsDefaultNames"));
        UserData.template = prop.getProperty("templateDefaultName");
        UserData.thirdPartyLibs = splitCSVSet(prop.getProperty("platformsDefaultNames"));
        UserData.libgdxVersion = prop.getProperty("libgdxDefaultVersion");
        UserData.javaVersion = prop.getProperty("javaDefaultVersion");
        appVersion = prop.getProperty("appDefaultVersion");
        androidPluginVersion = prop.getProperty("androidPluginDefaultVersion");
        UserData.robovmVersion = prop.getProperty("robovmDefaultVersion");
        gwtPluginVersion = prop.getProperty("gwtPluginDefaultVersion");
        UserData.addGuiAssets = Boolean.parseBoolean(prop.getProperty("addGuiAssetsDefault"));
        UserData.addReadme = Boolean.parseBoolean(prop.getProperty("addReadmeDefault"));
        UserData.gradleTasks = prop.getProperty("gradleTasksDefault");
    }

    public static boolean validateUserData() {
        if (UserData.projectName.isEmpty()) {
            return false;
        }

        if (UserData.packageName.isEmpty()) {
            return false;
        }

        if (!isValidPackageName(UserData.packageName)) {
            return false;
        }

        if (UserData.mainClassName.isEmpty()) {
            return false;
        }

        if (!isValidClassName(UserData.mainClassName)) {
            return false;
        }

        if (UserData.projectPath == null || UserData.projectPath.isEmpty()) {
            return false;
        }

        FileHandle tempFileHandle = Gdx.files.absolute(UserData.projectPath);
        if (!tempFileHandle.exists() || !tempFileHandle.isDirectory()) {
            return false;
        }

//        if (tempFileHandle.list().length != 0) {
//            return false;
//        }

        boolean android = UserData.platforms.contains("android");
        if (android && (UserData.androidPath == null || UserData.androidPath.isEmpty())) {
            return false;
        }

        tempFileHandle = Gdx.files.absolute(UserData.androidPath);
        if (android && (!tempFileHandle.exists() || !tempFileHandle.isDirectory())) {
            return false;
        }

        if (android && !Main.isAndroidSdkDirectory(UserData.androidPath)) {
            return false;
        }

        return true;
    }

    /**
     * Placeholder for project generation.
     */
    public static void generateProject() {
        generatingProject = true;
        Thread generateThread = new Thread(() -> {
            try {
                BasicProjectData basicData = new BasicProjectData(
                    UserData.projectName, UserData.packageName, UserData.mainClassName,
                    Gdx.files.absolute(UserData.projectPath), Gdx.files.absolute(UserData.androidPath));
                AdvancedProjectData advancedData = new AdvancedProjectData(appVersion, libgdxVersion, javaVersion,
                    androidPluginVersion, robovmVersion,
                    gwtPluginVersion, javaVersion, javaVersion, addGuiAssets, addReadme,
                    gradleTasks != null && !gradleTasks.isEmpty()
                        ? Arrays.asList(gradleTasks.split("\\s+"))
                        : Collections.emptyList(),
                    true, 4);

                LinkedHashMap<String, Platform> platforms = new LinkedHashMap<>(UserData.platforms.size());
                for (String p : UserData.platforms) {
                    platforms.put(p, Listing.platformsByName.get(p));
                }
                LanguagesData languagesData = new LanguagesData(Listing.chooseLanguages(languages), UserData.languageVersions);
                ExtensionsData extensionsData = new ExtensionsData(Listing.chooseOfficialLibraries(extensions),
                    Listing.chooseUnofficialLibraries(thirdPartyLibs));

                Project project = new Project(basicData, platforms, advancedData, languagesData, extensionsData,
                    Listing.templatesByName.getOrDefault(template, Listing.templates.get(0)));
                project.generate();
                project.includeGradleWrapper(new ProjectLogger() {
                    @Override
                    public void log(@NotNull String message) {
                        System.out.println(message);
                    }

                    @Override
                    public void logNls(@NotNull String bundleLine) {
                        System.out.println(prop.getProperty(bundleLine, "???"));
                    }
                }, false);
                log = prop.getProperty("generationEnd");
                generatingProject = false;
            } catch (Exception e) {
                log = exceptionToString(e) + "\n\n" + prop.getProperty("generationFail");
                generatingProject = false;
            }
        });
        generateThread.start();
    }

    private final static HashSet<String> BLOCKED_TYPES = new HashSet<>(Arrays.asList("absolutefilehandleresolver", "abstractgraphics", "abstractinput", "action", "actions", "actor", "actorgesturelistener", "addaction", "addlisteneraction", "affine2", "afteraction", "align", "alphaaction", "ambientcubemap", "animatedtiledmaptile", "animation", "animation", "animationcontroller", "annotation", "application", "applicationadapter", "applicationlistener", "applicationlogger", "array", "arraymap", "arrayreflection", "arrayselection", "arraytexturespritebatch", "arrowshapebuilder", "assetdescriptor", "asseterrorlistener", "assetloader", "assetloaderparameters", "assetmanager", "asyncexecutor", "asynchronousassetloader", "asyncresult", "asynctask", "atlastmxmaploader", "atomicqueue", "attribute", "attributes", "audio", "audiodevice", "audiorecorder", "base", "base64coder", "baseanimationcontroller", "basedrawable", "basejsonreader", "baselight", "baseshader", "baseshaderprovider", "baseshapebuilder", "basetmxmaploader", "batch", "batchtiledmaprenderer", "bezier", "billboardcontrollerrenderdata", "billboardparticlebatch", "billboardrenderer", "binaryheap", "bintree", "bitmapfont", "bitmapfontcache", "bitmapfontloader", "bits", "bittreedecoder", "bittreeencoder", "blendingattribute", "booleanarray", "boundingbox", "boxshapebuilder", "bresenham2", "bspline", "bufferedparticlebatch", "bufferutils", "button", "buttongroup", "bytearray", "camera", "cameragroupstrategy", "camerainputcontroller", "capsuleshapebuilder", "catmullromspline", "cell", "changelistener", "chararray", "checkbox", "circle", "circlemapobject", "classpathfilehandleresolver", "classreflection", "clicklistener", "clipboard", "collections", "color", "coloraction", "colorattribute", "colorinfluencer", "colors", "coneshapebuilder", "constructor", "container", "convexhull", "countdowneventaction", "cpuspritebatch", "crc", "cubemap", "cubemapattribute", "cubemapdata", "cubemaploader", "cullable", "cumulativedistribution", "cursor", "customtexture3ddata", "cylindershapebuilder", "cylinderspawnshapevalue", "databuffer", "datainput", "dataoutput", "decal", "decalbatch", "decalmaterial", "decoder", "decoder", "defaultrenderablesorter", "defaultshader", "defaultshaderprovider", "defaulttexturebinder", "delaunaytriangulator", "delayaction", "delayedremovalarray", "delegateaction", "depthshader", "depthshaderprovider", "depthtestattribute", "dialog", "directionallight", "directionallightsattribute", "directionalshadowlight", "disableable", "disposable", "distancefieldfont", "draganddrop", "draglistener", "dragscrolllistener", "drawable", "dynamicsinfluencer", "dynamicsmodifier", "earclippingtriangulator", "ellipse", "ellipsemapobject", "ellipseshapebuilder", "ellipsespawnshapevalue", "emitter", "encoder", "encoder", "environment", "etc1", "etc1texturedata", "event", "eventaction", "eventlistener", "extendviewport", "externalfilehandleresolver", "facedcubemapdata", "field", "filehandle", "filehandleresolver", "filehandlestream", "files", "filetexturearraydata", "filetexturedata", "fillviewport", "firstpersoncameracontroller", "fitviewport", "floataction", "floatarray", "floatattribute", "floatcounter", "floatframebuffer", "floattexturedata", "flushablepool", "focuslistener", "fpslogger", "framebuffer", "framebuffercubemap", "frustum", "frustumshapebuilder", "g3dmodelloader", "game", "gdx", "gdx2dpixmap", "gdxnativesloader", "gdxruntimeexception", "geometryutils", "gesturedetector", "gl20", "gl20interceptor", "gl30", "gl30interceptor", "gl31", "gl31interceptor", "gl32", "gl32interceptor", "glerrorlistener", "glframebuffer", "glinterceptor", "glonlytexturedata", "glprofiler", "gltexture", "glversion", "glyphlayout", "gradientcolorvalue", "graphics", "gridpoint2", "gridpoint3", "group", "groupplug", "groupstrategy", "hdpimode", "hdpiutils", "hexagonaltiledmaprenderer", "horizontalgroup", "httpparametersutils", "httprequestbuilder", "httprequestheader", "httpresponseheader", "httpstatus", "i18nbundle", "i18nbundleloader", "icodeprogress", "identitymap", "image", "imagebutton", "imageresolver", "imagetextbutton", "immediatemoderenderer", "immediatemoderenderer20", "indexarray", "indexbufferobject", "indexbufferobjectsubdata", "indexdata", "influencer", "input", "inputadapter", "inputevent", "inputeventqueue", "inputlistener", "inputmultiplexer", "inputprocessor", "instancebufferobject", "instancebufferobjectsubdata", "instancedata", "intaction", "intarray", "intattribute", "internalfilehandleresolver", "interpolation", "intersector", "intfloatmap", "intintmap", "intmap", "intset", "inwindow", "isometricstaggeredtiledmaprenderer", "isometrictiledmaprenderer", "json", "jsonreader", "jsonvalue", "jsonwriter", "ktxtexturedata", "label", "layout", "layoutaction", "lifecyclelistener", "linespawnshapevalue", "list", "littleendianinputstream", "localfilehandleresolver", "logger", "longarray", "longmap", "longqueue", "lzma", "map", "mapgrouplayer", "maplayer", "maplayers", "mapobject", "mapobjects", "mapproperties", "maprenderer", "material", "mathutils", "matrix3", "matrix4", "mesh", "meshbuilder", "meshpart", "meshpartbuilder", "meshspawnshapevalue", "method", "mipmapgenerator", "mipmaptexturedata", "model", "modelanimation", "modelbatch", "modelbuilder", "modelcache", "modeldata", "modelinfluencer", "modelinstance", "modelinstancecontrollerrenderdata", "modelinstanceparticlebatch", "modelinstancerenderer", "modelloader", "modelmaterial", "modelmesh", "modelmeshpart", "modelnode", "modelnodeanimation", "modelnodekeyframe", "modelnodepart", "modeltexture", "movebyaction", "movetoaction", "music", "musicloader", "nativeinputconfiguration", "net", "netjavaimpl", "netjavaserversocketimpl", "netjavasocketimpl", "ninepatch", "ninepatchdrawable", "node", "nodeanimation", "nodekeyframe", "nodepart", "null", "numberutils", "numericvalue", "objectfloatmap", "objectintmap", "objectlongmap", "objectmap", "objectset", "objloader", "octree", "orderedmap", "orderedset", "orientedboundingbox", "orthocachedtiledmaprenderer", "orthogonaltiledmaprenderer", "orthographiccamera", "outwindow", "parallelaction", "parallelarray", "particlebatch", "particlechannels", "particlecontroller", "particlecontrollercomponent", "particlecontrollercontrollerrenderer", "particlecontrollerfinalizerinfluencer", "particlecontrollerinfluencer", "particlecontrollerrenderdata", "particlecontrollerrenderer", "particleeffect", "particleeffect", "particleeffectactor", "particleeffectloader", "particleeffectloader", "particleeffectpool", "particleemitter", "particleshader", "particlesorter", "particlesystem", "particlevalue", "patchshapebuilder", "path", "pauseablethread", "performancecounter", "performancecounters", "perspectivecamera", "pixmap", "pixmapio", "pixmaploader", "pixmappacker", "pixmappackerio", "pixmaptexturedata", "plane", "pluggablegroupstrategy", "pointlight", "pointlightsattribute", "pointspawnshapevalue", "pointspritecontrollerrenderdata", "pointspriteparticlebatch", "pointspriterenderer", "polygon", "polygonbatch", "polygonmapobject", "polygonregion", "polygonregionloader", "polygonsprite", "polygonspritebatch", "polyline", "polylinemapobject", "pool", "pooledlinkedlist", "pools", "predicate", "preferences", "prefixfilehandleresolver", "primitivespawnshapevalue", "progressbar", "propertiesutils", "quadtreefloat", "quaternion", "queue", "quickselect", "randomxs128", "rangednumericvalue", "ray", "rectangle", "rectanglemapobject", "rectanglespawnshapevalue", "reflectionexception", "reflectionpool", "regioninfluencer", "regularemitter", "relativetemporalaction", "remoteinput", "remotesender", "removeaction", "removeactoraction", "removelisteneraction", "renderable", "renderableprovider", "renderableshapebuilder", "renderablesorter", "rendercontext", "repeatablepolygonsprite", "repeataction", "resolutionfileresolver", "resourcedata", "rotatebyaction", "rotatetoaction", "runnableaction", "scalebyaction", "scalednumericvalue", "scaleinfluencer", "scaletoaction", "scaling", "scalingviewport", "scissorstack", "screen", "screenadapter", "screenutils", "screenviewport", "scrollpane", "segment", "select", "selectbox", "selection", "sequenceaction", "serializationexception", "serversocket", "serversockethints", "shader", "shaderprogram", "shaderprogramloader", "shaderprovider", "shadowmap", "shape2d", "shapecache", "shaperenderer", "shortarray", "simpleinfluencer", "simpleorthogroupstrategy", "sizebyaction", "sizetoaction", "skin", "skinloader", "slider", "snapshotarray", "socket", "sockethints", "sort", "sortedintlist", "sound", "soundloader", "spawninfluencer", "spawnshapevalue", "sphere", "sphereshapebuilder", "sphericalharmonics", "splitpane", "spotlight", "spotlightsattribute", "sprite", "spritebatch", "spritecache", "spritedrawable", "stack", "stage", "statictiledmaptile", "streamutils", "stretchviewport", "stringbuilder", "synchronousassetloader", "table", "temporalaction", "textarea", "textbutton", "textfield", "textinputwrapper", "texttooltip", "texture", "texture3d", "texture3ddata", "texturearray", "texturearraydata", "textureatlas", "textureatlasloader", "textureattribute", "texturebinder", "texturedata", "texturedescriptor", "textureloader", "texturemapobject", "textureprovider", "textureregion", "textureregiondrawable", "threadutils", "tidemaploader", "tileddrawable", "tiledmap", "tiledmapimagelayer", "tiledmaprenderer", "tiledmaptile", "tiledmaptilelayer", "tiledmaptilemapobject", "tiledmaptileset", "tiledmaptilesets", "timer", "timescaleaction", "timeutils", "tmxmaploader", "tooltip", "tooltipmanager", "touchable", "touchableaction", "touchpad", "transformdrawable", "tree", "ubjsonreader", "ubjsonwriter", "uiutils", "unweightedmeshspawnshapevalue", "value", "vector", "vector2", "vector3", "vector4", "version", "vertexarray", "vertexattribute", "vertexattributes", "vertexbufferobject", "vertexbufferobjectsubdata", "vertexbufferobjectwithvao", "vertexdata", "verticalgroup", "viewport", "visibleaction", "weightmeshspawnshapevalue", "widget", "widgetgroup", "window", "windowedmean", "xmlreader", "xmlwriter"));
    private final static String[] FORBIDDEN_NAMES = {"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "false", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while", "_", "con", "prn", "aux", "nul", "com1", "com2", "com3", "com4", "com5", "com6", "com7", "com8", "com9", "lpt1", "lpt2", "lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9"};

    public static boolean isValidPackageName(String input) {
        if (input == null || input.isEmpty() || !Character.isJavaIdentifierStart(input.charAt(0)) || input.contains("..") || input.endsWith(".")) {
            return false;
        }

        boolean previousDot = false;
        for (int id = 1; id < input.length(); id++) {
            if (input.charAt(id) == '.') {
                previousDot = true;
            } else {
                if ((previousDot && !Character.isJavaIdentifierStart(input.charAt(id))) || !Character.isJavaIdentifierPart(input.charAt(id))) {
                    return false;
                }
                previousDot = false;
            }
        }

        // case-insensitive check for any Java reserved word, then keep checking for Win32 reserved file/folder names.
        boolean containsForbiddenName = false;
        for (String forbiddenName : FORBIDDEN_NAMES) {
            if (input.matches("(?i).*(^|\\.)" + forbiddenName + "(\\.|$).*")) {
                containsForbiddenName = true;
                break;
            }
        }

        return !(!input.contains(".") || containsForbiddenName);
    }

    public static boolean isValidClassName(String input) {
        if (input == null || input.isEmpty() || !Character.isJavaIdentifierStart(input.charAt(0))) {
            return false;
        } else if (input.length() == 1) {
            return true;
        }
        for (int i = 1; i < input.length(); i++) {
            if (!Character.isJavaIdentifierPart(input.charAt(i))) {
                return false;
            }
        }
        boolean containsForbiddenName = false;
        for (String forbiddenName : FORBIDDEN_NAMES) {
            if (input.matches("(?i)" + forbiddenName)) {
                containsForbiddenName = true;
                break;
            }
        }

        return !containsForbiddenName && !BLOCKED_TYPES.contains(input.toLowerCase());
    }

    public static boolean isAndroidSdkDirectory(String path) {
        try {
            FileHandle file = Gdx.files.absolute(path);
            if (file.isDirectory()) {
                return (file.child("tools").isDirectory() ||
                    file.child("cmdline-tools").isDirectory() ||
                    file.child("build-tools").isDirectory()) &&
                    file.child("platforms").isDirectory();
            }
        } catch (Exception exception) {
            // Probably not the Android SDK.
        }
        return false;
    }

    public static void checkSetupVersion() {
        // When using snapshots, we don't care if the version matches the latest stable.
        if (prop.getProperty("liftoffVersion").endsWith("SNAPSHOT")) return;

        HttpRequest request = new HttpRequestBuilder().newRequest()
            .method("GET")
            .url("https://raw.githubusercontent.com/libgdx/gdx-liftoff/master/version.txt")
            .build();

        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                latestStableVersion = httpResponse.getResultAsString().trim();
                if (!prop.getProperty("liftoffVersion").equals(latestStableVersion)) {
                    Gdx.app.postRunnable(() -> {
                        root.landingTable.animateUpdateLabel();
                        if (fullscreenDialog != null) fullscreenDialog.updateVersion();
                    });
                }
            }

            @Override
            public void cancelled() {
                // Never cancelled.
            }

            @Override
            public void failed(Throwable t) {
                // Ignored. The user might not be connected.
            }
        };

        Gdx.net.sendHttpRequest(request, listener);
    }
}
