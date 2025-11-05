package gdx.liftoff.data.templates

import gdx.liftoff.config.GdxVersion
import gdx.liftoff.data.files.SourceFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.platforms.Android
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.platforms.Core
import gdx.liftoff.data.platforms.GWT
import gdx.liftoff.data.platforms.Headless
import gdx.liftoff.data.platforms.IOS
import gdx.liftoff.data.platforms.IOSMOE
import gdx.liftoff.data.platforms.Lwjgl2
import gdx.liftoff.data.platforms.Lwjgl3
import gdx.liftoff.data.platforms.Server
import gdx.liftoff.data.platforms.TeaVM
import gdx.liftoff.data.project.Project

/**
 * Interface shared by all project templates. Templates should be annotated with ProjectTemplate.
 */
interface Template {
  val id: String

  // Sizes are kept as strings, so you can set the sizes to static values, for example: MainClass.WIDTH.
  val width: String
    get() = "640"
  val height: String
    get() = "480"

  /** Used as project description in README file. Optional. */
  val description: String
    get() = ""

  /** File extension of the ApplicationListener implementation. */
  val applicationListenerExtension: String
    get() = "java"

  /** File extension of the application launchers on each platform. */
  val launcherExtension: String
    get() = "java"

  val defaultSourceFolder: String
    get() = path("src", "main", "java")

  /**
   * @param project is being created. Should contain sources provided by this template.
   */
  fun apply(project: Project) {
    addApplicationListener(project)
    addAndroidLauncher(project)
    addLwjgl2Launcher(project)
    addGwtLauncher(project)
    addHeadlessLauncher(project)
    addIOSLauncher(project)
    addIOSMOELauncher(project)
    addLwjgl3Launcher(project)
    addServerLauncher(project)
    addTeaVMLauncher(project)
    project.readmeDescription = description
  }

  fun addApplicationListener(project: Project) {
    addSourceFile(
      project = project,
      platform = Core.ID,
      packageName = project.basic.rootPackage,
      fileName = "${project.basic.mainClass}.$applicationListenerExtension",
      content = getApplicationListenerContent(project),
    )
  }

  /**
   * @param project is being created.
   * @return content of Java class implementing ApplicationListener.
   */
  fun getApplicationListenerContent(project: Project): String

  fun addLwjgl2Launcher(project: Project) {
    addSourceFile(
      project = project,
      platform = Lwjgl2.ID,
      packageName = "${project.basic.rootPackage}.lwjgl2",
      fileName = "Lwjgl2Launcher.$launcherExtension",
      content = getLwjgl2LauncherContent(project),
    )
  }

  fun getLwjgl2LauncherContent(project: Project): String =
    """package ${project.basic.rootPackage}.lwjgl2;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Launches the desktop (LWJGL2) application. */
public class Lwjgl2Launcher {
    public static void main(String[] args) {
        createApplication();
    }

    private static LwjglApplication createApplication() {
        return new LwjglApplication(new ${project.basic.mainClass}(), getDefaultConfiguration());
    }

    private static LwjglApplicationConfiguration getDefaultConfiguration() {
        LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.title = "${project.basic.name}";
        configuration.width = $width;
        configuration.height = $height;
        //// This prevents a confusing error that would appear after exiting normally.
        configuration.forceExit = false;

        for (int size : new int[] { 128, 64, 32, 16 }) {
            configuration.addIcon("libgdx" + size + ".png", FileType.Internal);
        }
        return configuration;
    }
}"""

  fun addGwtLauncher(project: Project) {
    addSourceFile(
      project = project,
      platform = GWT.ID,
      packageName = "${project.basic.rootPackage}.gwt",
      content = getGwtLauncherContent(project),
      // GWT supports only Java sources:
      fileName = "GwtLauncher.java",
      sourceFolderPath = path("src", "main", "java"),
    )
  }

  fun getGwtLauncherContent(project: Project): String =
    """package ${project.basic.rootPackage}.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {""" + (
      if (GdxVersion.parseGdxVersion(project.advanced.gdxVersion) != null && GdxVersion.parseGdxVersion(project.advanced.gdxVersion)!! < GdxVersion(1, 9, 12)) {
        """
        ////USE THIS CODE FOR A FIXED SIZE APPLICATION
        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration($width, $height);
        }
        ////END CODE FOR FIXED SIZE APPLICATION

        ////UNCOMMENT THIS CODE FOR A RESIZABLE APPLICATION
        //    PADDING is to avoid scrolling in iframes, set to 20 if you have problems
        //    private static final int PADDING = 0;
        //
        //    @Override
        //    public GwtApplicationConfiguration getConfig() {
        //        int w = Window.getClientWidth() - PADDING;
        //        int h = Window.getClientHeight() - PADDING;
        //        GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(w, h);
        //        Window.enableScrolling(false);
        //        Window.setMargin("0");
        //        Window.addResizeHandler(new ResizeListener());
        //        cfg.preferFlash = false;
        //        return cfg;
        //    }
        //
        //    class ResizeListener implements ResizeHandler {
        //        @Override
        //        public void onResize(ResizeEvent event) {
        //            if (Gdx.graphics.isFullscreen()) return;
        //            int width = event.getWidth() - PADDING;
        //            int height = event.getHeight() - PADDING;
        //            getRootPanel().setWidth("" + width + "px");
        //            getRootPanel().setHeight("" + height + "px");
        //            getApplicationListener().resize(width, height);
        //            Gdx.graphics.setWindowedMode(width, height);
        //        }
        //    }
        ////END OF CODE FOR RESIZABLE APPLICATION
"""
      } else {
        """
        @Override
        public GwtApplicationConfiguration getConfig () {
            // Resizable application, uses available space in browser with no padding:
            GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(true);
            cfg.padVertical = 0;
            cfg.padHorizontal = 0;
            return cfg;
            // If you want a fixed size application, comment out the above resizable section,
            // and uncomment below:
            //return new GwtApplicationConfiguration($width, $height);
        }
"""
      }
    ) +
"""
        @Override
        public ApplicationListener createApplicationListener () {
            return new ${project.basic.mainClass}();
        }
}
"""

  fun addAndroidLauncher(project: Project) {
    addSourceFile(
      project = project,
      platform = Android.ID,
      packageName = project.basic.rootPackage,
      fileName = "android/AndroidLauncher.$launcherExtension",
      content = getAndroidLauncherContent(project),
    )
  }

  fun getAndroidLauncherContent(project: Project): String =
    """package ${project.basic.rootPackage}.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.
        initialize(new ${project.basic.mainClass}(), configuration);
    }
}"""

  fun addHeadlessLauncher(project: Project) {
    addSourceFile(
      project = project,
      platform = Headless.ID,
      packageName = "${project.basic.rootPackage}.headless",
      fileName = "HeadlessLauncher.$launcherExtension",
      content = getHeadlessLauncherContent(project),
    )
  }

  fun getHeadlessLauncherContent(project: Project): String =
    """package ${project.basic.rootPackage}.headless;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Launches the headless application. Can be converted into a utilities project or a server application. */
public class HeadlessLauncher {
    public static void main(String[] args) {
        createApplication();
    }

    private static Application createApplication() {
        // Note: you can use a custom ApplicationListener implementation for the headless project instead of ${project.basic.mainClass}.
        return new HeadlessApplication(new ${project.basic.mainClass}(), getDefaultConfiguration());
    }

    private static HeadlessApplicationConfiguration getDefaultConfiguration() {
        HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
        configuration.updatesPerSecond = -1; // When this value is negative, ${project.basic.mainClass}#render() is never called.
        //// If the above line doesn't compile, it is probably because the project libGDX version is older.
        //// In that case, uncomment and use the below line.
        //configuration.renderInterval = -1f; // When this value is negative, ${project.basic.mainClass}#render() is never called.
        return configuration;
    }
}"""

  fun addIOSLauncher(project: Project) {
    addSourceFile(
      project = project,
      platform = IOS.ID,
      packageName = project.basic.rootPackage,
      fileName = "IOSLauncher.$launcherExtension",
      content = getIOSLauncherContent(project),
    )
  }

  fun getIOSLauncherContent(project: Project): String =
    """package ${project.basic.rootPackage};

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Launches the iOS (RoboVM) application. */
public class IOSLauncher extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration configuration = new IOSApplicationConfiguration();
        return new IOSApplication(new ${project.basic.mainClass}(), configuration);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}"""

  fun addIOSMOELauncher(project: Project) {
    addSourceFile(
      project = project,
      platform = IOSMOE.ID,
      packageName = project.basic.rootPackage,
      fileName = "IOSLauncher.$launcherExtension",
      content = getIOSMOELauncherContent(project),
    )
  }

  fun getIOSMOELauncherContent(project: Project): String =
    """package ${project.basic.rootPackage};

import apple.uikit.c.UIKit;
import com.badlogic.gdx.backends.iosmoe.IOSApplication;
import com.badlogic.gdx.backends.iosmoe.IOSApplicationConfiguration;
import org.moe.natj.general.Pointer;
import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Launches the iOS (Multi-Os Engine) application. */
public class IOSLauncher extends IOSApplication.Delegate {
    protected IOSLauncher(Pointer peer) {
        super(peer);
    }

    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration configuration = new IOSApplicationConfiguration();
        return new IOSApplication(new ${project.basic.mainClass}(), configuration);
    }

    public static void main(String[] argv) {
        UIKit.UIApplicationMain(0, null, null, IOSLauncher.class.getName());
    }
}"""

  fun addLwjgl3Launcher(project: Project) {
    addSourceFile(
      project = project,
      platform = Lwjgl3.ID,
      packageName = "${project.basic.rootPackage}.lwjgl3",
      fileName = "Lwjgl3Launcher.$launcherExtension",
      content = getLwjgl3LauncherContent(project),
    )
    addSourceFile(
      project = project,
      platform = Lwjgl3.ID,
      packageName = "${project.basic.rootPackage}.lwjgl3",
      fileName = "StartupHelper.$launcherExtension",
      content = getLwjgl3StartupContent(project),
    )
  }

  fun getLwjgl3LauncherContent(project: Project): String =
    """package ${project.basic.rootPackage}.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new ${project.basic.mainClass}(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("${project.basic.name}");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

        configuration.setWindowedMode($width, $height);
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        //// This should improve compatibility with Windows machines with buggy OpenGL drivers, Macs
        //// with Apple Silicon that have to emulate compatibility with OpenGL anyway, and more.
        //// This uses the dependency `com.badlogicgames.gdx:gdx-lwjgl3-angle` to function.
        //// You can choose to remove the following line and the mentioned dependency if you want; they
        //// are not intended for games that use GL30 (which is compatibility with OpenGL ES 3.0).
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0);

        return configuration;
    }
}"""

  fun getLwjgl3StartupContent(project: Project): String =
    """/*
 * Copyright 2020 damios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//Note, the above license and copyright applies to this file only.

package ${project.basic.rootPackage}.lwjgl3;

import com.badlogic.gdx.Version;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import org.lwjgl.system.macosx.LibC;
import org.lwjgl.system.macosx.ObjCRuntime;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

import static org.lwjgl.system.JNI.invokePPP;
import static org.lwjgl.system.JNI.invokePPZ;
import static org.lwjgl.system.macosx.ObjCRuntime.objc_getClass;
import static org.lwjgl.system.macosx.ObjCRuntime.sel_getUid;

/**
 * Adds some utilities to ensure that the JVM was started with the
 * {@code -XstartOnFirstThread} argument, which is required on macOS for LWJGL 3
 * to function. Also helps on Windows when users have names with characters from
 * outside the Latin alphabet, a common cause of startup crashes.
 * <br>
 * <a href="https://jvm-gaming.org/t/starting-jvm-on-mac-with-xstartonfirstthread-programmatically/57547">Based on this java-gaming.org post by kappa</a>
 * @author damios
 */
public class StartupHelper {

    private static final String JVM_RESTARTED_ARG = "jvmIsRestarted";

    private StartupHelper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Starts a new JVM if the application was started on macOS without the
     * {@code -XstartOnFirstThread} argument. This also includes some code for
     * Windows, for the case where the user's home directory includes certain
     * non-Latin-alphabet characters (without this code, most LWJGL3 apps fail
     * immediately for those users). Returns whether a new JVM was started and
     * thus no code should be executed.
     * <p>
     * <u>Usage:</u>
     *
     * <pre><code>
     * public static void main(String... args) {
     * 	if (StartupHelper.startNewJvmIfRequired(true)) return; // This handles macOS support and helps on Windows.
     * 	// after this is the actual main method code
     * }
     * </code></pre>
     *
     * @param redirectOutput
     *            whether the output of the new JVM should be rerouted to the
     *            old JVM, so it can be accessed in the same place; keeps the
     *            old JVM running if enabled
     * @return whether a new JVM was started and thus no code should be executed
     *         in this one
     */
    public static boolean startNewJvmIfRequired(boolean redirectOutput) {
        String osName = System.getProperty("os.name").toLowerCase(java.util.Locale.ROOT);
        if (!osName.contains("mac")) {
            if (osName.contains("windows")) {
// Here, we are trying to work around an issue with how LWJGL3 loads its extracted .dll files.
// By default, LWJGL3 extracts to the directory specified by "java.io.tmpdir", which is usually the user's home.
// If the user's name has non-ASCII (or some non-alphanumeric) characters in it, that would fail.
// By extracting to the relevant "ProgramData" folder, which is usually "C:\ProgramData", we avoid this.
// We also temporarily change the "user.name" property to one without any chars that would be invalid.
// We revert our changes immediately after loading LWJGL3 natives.
                String programData = System.getenv("ProgramData");
                if(programData == null) programData = "C:\\Temp\\"; // if ProgramData isn't set, try some fallback.
                String prevTmpDir = System.getProperty("java.io.tmpdir", programData);
                String prevUser = System.getProperty("user.name", "libGDX_User");
                System.setProperty("java.io.tmpdir", programData + "/libGDX-temp");
                System.setProperty("user.name", ("User_" + prevUser.hashCode() + "_GDX" + Version.VERSION).replace('.', '_'));
                Lwjgl3NativesLoader.load();
                System.setProperty("java.io.tmpdir", prevTmpDir);
                System.setProperty("user.name", prevUser);
            }
            return false;
        }

        // There is no need for -XstartOnFirstThread on Graal native image
        if (!System.getProperty("org.graalvm.nativeimage.imagecode", "").isEmpty()) {
            return false;
        }

        // Checks if we are already on the main thread, such as from running via Construo.
        long objc_msgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");
        long NSThread      = objc_getClass("NSThread");
        long currentThread = invokePPP(NSThread, sel_getUid("currentThread"), objc_msgSend);
        boolean isMainThread = invokePPZ(currentThread, sel_getUid("isMainThread"), objc_msgSend);
        if(isMainThread) return false;

        long pid = LibC.getpid();

        // check whether -XstartOnFirstThread is enabled
        if ("1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + pid))) {
            return false;
        }

        // check whether the JVM was previously restarted
        // avoids looping, but most certainly leads to a crash
        if ("true".equals(System.getProperty(JVM_RESTARTED_ARG))) {
            System.err.println(
                    "There was a problem evaluating whether the JVM was started with the -XstartOnFirstThread argument.");
            return false;
        }

        // Restart the JVM with -XstartOnFirstThread
        ArrayList<String> jvmArgs = new ArrayList<>();
        String separator = System.getProperty("file.separator", "/");
        // The following line is used assuming you target Java 8, the minimum for LWJGL3.
        String javaExecPath = System.getProperty("java.home") + separator + "bin" + separator + "java";
        // If targeting Java 9 or higher, you could use the following instead of the above line:
        //String javaExecPath = ProcessHandle.current().info().command().orElseThrow();

        if (!(new File(javaExecPath)).exists()) {
            System.err.println(
                    "A Java installation could not be found. If you are distributing this app with a bundled JRE, be sure to set the -XstartOnFirstThread argument manually!");
            return false;
        }

        jvmArgs.add(javaExecPath);
        jvmArgs.add("-XstartOnFirstThread");
        jvmArgs.add("-D" + JVM_RESTARTED_ARG + "=true");
        jvmArgs.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
        jvmArgs.add("-cp");
        jvmArgs.add(System.getProperty("java.class.path"));
        String mainClass = System.getenv("JAVA_MAIN_CLASS_" + pid);
        if (mainClass == null) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            if (trace.length > 0) {
                mainClass = trace[trace.length - 1].getClassName();
            } else {
                System.err.println("The main class could not be determined.");
                return false;
            }
        }
        jvmArgs.add(mainClass);

        try {
            if (!redirectOutput) {
                ProcessBuilder processBuilder = new ProcessBuilder(jvmArgs);
                processBuilder.start();
            } else {
                Process process = (new ProcessBuilder(jvmArgs))
                        .redirectErrorStream(true).start();
                BufferedReader processOutput = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line;

                while ((line = processOutput.readLine()) != null) {
                    System.out.println(line);
                }

                process.waitFor();
            }
        } catch (Exception e) {
            System.err.println("There was a problem restarting the JVM");
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Starts a new JVM if the application was started on macOS without the
     * {@code -XstartOnFirstThread} argument. Returns whether a new JVM was
     * started and thus no code should be executed. Redirects the output of the
     * new JVM to the old one.
     * <p>
     * <u>Usage:</u>
     *
     * <pre>
     * public static void main(String... args) {
     * 	if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
     * 	// the actual main method code
     * }
     * </pre>
     *
     * @return whether a new JVM was started and thus no code should be executed
     *         in this one
     */
    public static boolean startNewJvmIfRequired() {
        return startNewJvmIfRequired(true);
    }
}"""

  fun addServerLauncher(project: Project) {
    addSourceFile(
      project = project,
      platform = Server.ID,
      packageName = "${project.basic.rootPackage}.server",
      fileName = "ServerLauncher.$launcherExtension",
      content = getServerLauncherContent(project),
    )
  }

  fun getServerLauncherContent(project: Project): String =
    """package ${project.basic.rootPackage}.server;

/** Launches the server application. */
public class ServerLauncher {
    public static void main(String[] args) {
        // TODO Implement server application.
    }
}"""

  fun addTeaVMLauncher(project: Project) {
    addSourceFile(
      project = project,
      platform = TeaVM.ID,
      packageName = "${project.basic.rootPackage}.teavm",
      fileName = "TeaVMLauncher.$launcherExtension",
      content = getTeaVMLauncherContent(project),
    )
    addSourceFile(
      project = project,
      platform = TeaVM.ID,
      packageName = "${project.basic.rootPackage}.teavm",
      fileName = "TeaVMBuilder.$launcherExtension",
      content = getTeaVMBuilderContent(project),
    )
  }

  fun getTeaVMLauncherContent(project: Project): String =
    """package ${project.basic.rootPackage}.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import ${project.basic.rootPackage}.${project.basic.mainClass};

/**
 * Launches the TeaVM/HTML application.
 */
public class TeaVMLauncher {
    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        //// If width and height are each greater than 0, then the app will use a fixed size.
        //config.width = $width;
        //config.height = $height;
        //// If width and height are both 0, then the app will use all available space.
        config.width = 0;
        config.height = 0;
        new TeaApplication(new ${project.basic.mainClass}(), config);
    }
}
"""

  fun getTeaVMBuilderContent(project: Project): String =
    """package ${project.basic.rootPackage}.teavm;

import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.config.plugins.TeaReflectionSupplier;
import java.io.File;
import java.io.IOException;
import org.teavm.backend.wasm.WasmDebugInfoLevel;
import org.teavm.tooling.TeaVMSourceFilePolicy;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.sources.DirectorySourceFileProvider;
import org.teavm.vm.TeaVMOptimizationLevel;

/** Builds the TeaVM/HTML application. */
public class TeaVMBuilder {
    /**
     * A single point to configure most debug vs. release settings.
     * This defaults to false in new projects; set this to false when you want to release.
     * If this is true, the output will not be obfuscated, and debug information will usually be produced.
     * You can still set obfuscation to false in a release if you want the source to be at least a little legible.
     * This works well when the targetType is set to JAVASCRIPT, but you can still set the targetType to WEBASSEMBLY_GC
     * while this is true in order to test that higher-performance target before releasing.
     */
    private static final boolean DEBUG = true;

    public static void main(String[] args) throws IOException {
        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
        teaBuildConfiguration.assetsPath.add(new AssetFileHandle("../${Assets.ID}"));
        teaBuildConfiguration.webappPath = new File("build/dist").getCanonicalPath();

        // Register any extra classpath assets here:
        // teaBuildConfiguration.additionalAssetsClasspathFiles.add("${project.basic.rootPackage.replace('.', '/')}/asset.extension");

        // If you need to match specific classes based on the package and class name,
        // you can use the reflectionListener to do fine-grained matching on the String fullClassName.
//        teaBuildConfiguration.reflectionListener = fullClassName -> {
//            if(fullClassName.startsWith("where.your.reflective.code.is") && fullClassName.endsWith("YourSuffix"))
//                return true;
//            return false;
//        };

        // You can also register any classes or packages that require reflection here:
${generateTeaVMReflectionIncludes(project)}

        // Used by older TeaVM versions.
//        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration);

        // The next two lines are used by gdx-teavm 1.3.1 and newer (and libGDX 1.14.0 and newer).
        TeaBuilder.config(teaBuildConfiguration);
        TeaVMTool tool = new TeaVMTool();

        // JavaScript is the default target type for TeaVM, and it works better during debugging.
        tool.setTargetType(TeaVMTargetType.JAVASCRIPT);
        // You can choose to use the WebAssembly (WASM) GC target instead, which tends to perform better, but isn't
        // as easy to debug. It might be a good idea to alternate target types during development if you plan on using
        // WASM at release time.
//        tool.setTargetType(TeaVMTargetType.WEBASSEMBLY_GC);

        tool.setMainClass(TeaVMLauncher.class.getName());
        // For many (or most) applications, using a high optimization won't add much to build time.
        // If your builds take too long, and runtime performance doesn't matter, you can change ADVANCED to SIMPLE .
        // Using SIMPLE makes debugging easier, also, so it is used when DEBUG is enabled.
        tool.setOptimizationLevel(DEBUG ? TeaVMOptimizationLevel.SIMPLE : TeaVMOptimizationLevel.ADVANCED);
        // The line below will make the generated code hard to read (and smaller) in releases and easier to follow
        // when DEBUG is true. Setting DEBUG to false should always be done before a release, anyway.
        tool.setObfuscated(!DEBUG);

        // If DEBUG is set to true, these lines allow step-debugging JVM languages from the browser,
        // setting breakpoints in Java code and stopping in the appropriate place in generated browser code.
        // This may work reasonably well when targeting WEBASSEMBLY_GC, but it usually works better with JAVASCRIPT .
        if(DEBUG) {
            tool.setDebugInformationGenerated(true);
            tool.setSourceMapsFileGenerated(true);
            tool.setWasmDebugInfoLevel(WasmDebugInfoLevel.FULL);
            tool.setSourceFilePolicy(TeaVMSourceFilePolicy.COPY);
            tool.addSourceFileProvider(new DirectorySourceFileProvider(new File("../core/src/main/java/")));
        }

        TeaBuilder.build(tool);
    }
}
"""

  fun generateTeaVMReflectionIncludes(
    project: Project,
    indent: String = " ".repeat(8),
    trailingSemicolon: Boolean = true,
  ): String {
    val semicolon = if (trailingSemicolon) ";" else ""
    return if (project.reflectedPackages.isEmpty() && project.reflectedClasses.isEmpty()) {
      "$indent// TeaReflectionSupplier.addReflectionClass(\"${project.basic.rootPackage}.reflect\")$semicolon"
    } else {
      (project.reflectedPackages + project.reflectedClasses).joinToString(separator = "\n") {
        "${indent}TeaReflectionSupplier.addReflectionClass(\"$it\")$semicolon"
      }
    }
  }

  fun addSourceFile(
    project: Project,
    platform: String,
    packageName: String,
    fileName: String,
    content: String,
    sourceFolderPath: String = defaultSourceFolder,
  ) {
    if (project.hasPlatform(platform)) {
      project.files.add(
        SourceFile(
          projectName = platform,
          sourceFolderPath = sourceFolderPath,
          packageName = packageName,
          fileName = fileName,
          content = content,
        ),
      )
    }
  }
}
