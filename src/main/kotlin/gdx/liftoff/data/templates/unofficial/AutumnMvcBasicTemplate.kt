package gdx.liftoff.data.templates.unofficial

import gdx.liftoff.data.files.SourceFile
import gdx.liftoff.data.libraries.unofficial.AutumnMVC
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.project.Project
import gdx.liftoff.data.templates.Template
import gdx.liftoff.views.ProjectTemplate

/**
 * A project template showing basic Autumn MVC usage.
 */
@ProjectTemplate
open class AutumnMvcBasicTemplate : Template {
  override val id: String = "lmlMvcBasicTemplate"
  protected open val generateSkin = true
  override val description: String
    get() = "Project template included launchers with [Autumn](https://github.com/crashinvaders/gdx-lml/tree/master/autumn) " +
      "class scanners and a single [Autumn MVC](https://github.com/crashinvaders/gdx-lml/tree/master/mvc) view."

  override fun apply(project: Project) {
    // Registering main class in GWT/RoboVM reflection pool:
    getReflectedClasses(project).forEach { project.reflectedClasses.add(it) }
    getReflectedPackages(project).forEach { project.reflectedPackages.add(it) }

    super.apply(project)
    if (generateSkin) project.advanced.generateSkin = true

    // Adding Autumn MVC dependency:
    AutumnMVC().initiate(project)

    // Adding example LML template file:
    addViews(project)
  }

  protected open fun getReflectedClasses(project: Project): Array<String> =
    arrayOf("${project.basic.rootPackage}.${project.basic.mainClass}")

  protected open fun getReflectedPackages(project: Project): Array<String> =
    arrayOf()

  protected open fun addViews(project: Project) {
    project.files.add(
      SourceFile(
        projectName = Assets.ID,
        sourceFolderPath = "ui",
        packageName = "templates",
        fileName = "first.lml",
        content = """<!-- Note: you can get content assist thanks to DTD schema files. See the official LML page. -->
<window title="Example" style="border" defaultPad="4" oneColumn="true">
  This is a simple Autumn MVC view constructed with LML.
  <textButton onClick="setClicked" tablePad="8">Click me!</textButton>
</window>"""
      )
    )
  }

  override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.autumn.mvc.stereotype.preference.Skin;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.action.ActionContainer;

/** The first application's view. See first.lml file for widgets layout. */
@View(id = "first", value = "ui/templates/first.lml", first = true)
public class ${project.basic.mainClass} implements ActionContainer {
    /** Default application size. */
    public static final int WIDTH = 640, HEIGHT = 480;
    /** Path to the skin files. */
    @Skin private final String skinFile = "ui/uiskin";

    /** Since this method is annotated with LmlAction and this class implements ActionContainer, this method will be
     * available in the LML template: first.lml
     * @param button its text will be changed. */
    @LmlAction("setClicked")
    public void changeButtonText(TextButton button) {
        button.setText("Clicked.");
    }
}"""

  override fun getLwjgl2LauncherContent(project: Project): String = """package ${project.basic.rootPackage}.lwjgl2;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.czyzby.autumn.fcs.scanner.DesktopClassScanner;
import com.github.czyzby.autumn.mvc.application.AutumnApplication;
import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Launches the desktop (LWJGL2) application. */
public class Lwjgl2Launcher {
    public static void main(String[] args) {
        createApplication();
    }

    private static LwjglApplication createApplication() {
        return new LwjglApplication(new AutumnApplication(new DesktopClassScanner(), ${project.basic.mainClass}.class),
            getDefaultConfiguration());
    }

    private static LwjglApplicationConfiguration getDefaultConfiguration() {
        LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.title = "${project.basic.name}";
        configuration.width = ${project.basic.mainClass}.WIDTH;
        configuration.height = ${project.basic.mainClass}.HEIGHT;
        // This prevents a confusing error that would appear after exiting normally.
        configuration.forceExit = false;

        for (int size : new int[] { 128, 64, 32, 16 }) {
            configuration.addIcon("libgdx" + size + ".png", FileType.Internal);
        }
        return configuration;
    }
}"""

  override fun getLwjgl3LauncherContent(project: Project): String = """package ${project.basic.rootPackage}.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.czyzby.autumn.fcs.scanner.DesktopClassScanner;
import com.github.czyzby.autumn.mvc.application.AutumnApplication;
import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new AutumnApplication(new DesktopClassScanner(), ${project.basic.mainClass}.class),
            getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("${project.basic.name}");
        configuration.setWindowedMode(${project.basic.mainClass}.WIDTH, ${project.basic.mainClass}.HEIGHT);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}"""

  override fun getGwtLauncherContent(project: Project): String = """package ${project.basic.rootPackage}.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.github.czyzby.autumn.gwt.scanner.GwtClassScanner;
import com.github.czyzby.autumn.mvc.application.AutumnApplication;
import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration configuration = new GwtApplicationConfiguration(${project.basic.mainClass}.WIDTH, ${project.basic.mainClass}.HEIGHT);
        return configuration;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new AutumnApplication(new GwtClassScanner(), ${project.basic.mainClass}.class);
    }
}"""

  override fun getAndroidLauncherContent(project: Project): String = """package ${project.basic.rootPackage}.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.github.czyzby.autumn.android.scanner.AndroidClassScanner;
import com.github.czyzby.autumn.mvc.application.AutumnApplication;
import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.
        initialize(new AutumnApplication(new AndroidClassScanner(), ${project.basic.mainClass}.class), configuration);
    }
}"""

  override fun getHeadlessLauncherContent(project: Project): String = """package ${project.basic.rootPackage}.headless;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.github.czyzby.autumn.fcs.scanner.DesktopClassScanner;
import com.github.czyzby.autumn.mvc.application.AutumnApplication;
import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Launches the headless application. Can be converted into a utilities project or a server application. */
public class HeadlessLauncher {
    public static void main(String[] args) {
        createApplication();
    }

    private static Application createApplication() {
        // Note: you can use a custom ApplicationListener implementation for the headless project instead of ${project.basic.mainClass}.
        return new HeadlessApplication(new AutumnApplication(new DesktopClassScanner(), ${project.basic.mainClass}.class),
            getDefaultConfiguration());
    }

    private static HeadlessApplicationConfiguration getDefaultConfiguration() {
        HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
        configuration.updatesPerSecond = -1; // When this value is negative, ${project.basic.mainClass}#render() is never called.
        // If the above line doesn't compile, it is probably because the project libGDX version is older.
        // In that case, uncomment and use the below line.
        // configuration.renderInterval = -1f; // When this value is negative, ${project.basic.mainClass}#render() is never called.
        return configuration;
    }
}"""

  override fun getIOSLauncherContent(project: Project): String = """package ${project.basic.rootPackage}.ios;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.github.czyzby.autumn.mvc.application.AutumnApplication;
import com.github.czyzby.autumn.scanner.FixedClassScanner;
import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Launches the iOS (RoboVM) application. */
public class IOSLauncher extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration configuration = new IOSApplicationConfiguration();
        // Note: there is currently no automatic classpath scanning support on iOS. You have to register all component
        // classes manually with FixedClassScanner. Generated template might not work out of the box because of this.
        return new IOSApplication(new AutumnApplication(new FixedClassScanner(${project.basic.mainClass}.class),
            ${project.basic.mainClass}.class), configuration);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}"""
}
