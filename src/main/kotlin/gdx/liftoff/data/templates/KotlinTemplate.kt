package gdx.liftoff.data.templates

import gdx.liftoff.data.files.path
import gdx.liftoff.data.languages.Kotlin
import gdx.liftoff.data.project.Project

/**
 * Basic interface for Kotlin project templates. Adds a Kotlin launcher for each platform.
 */
interface KotlinTemplate : Template {
  override val applicationListenerExtension: String
    get() = "kt"
  override val launcherExtension: String
    get() = "kt"
  override val defaultSourceFolder: String
    get() = path("src", "main", "kotlin")

  override fun apply(project: Project) {
    super.apply(project)
    project.languages.selectLanguage<Kotlin>()
  }

  override fun getLwjgl2LauncherContent(project: Project): String = """@file:JvmName("Lwjgl2Launcher")

package ${project.basic.rootPackage}.lwjgl2

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import ${project.basic.rootPackage}.${project.basic.mainClass}

/** Launches the desktop (LWJGL) application. */
fun main() {
    LwjglApplication(${project.basic.mainClass}(), LwjglApplicationConfiguration().apply {
        title = "${project.basic.name}"
        width = $width
        height = $height
        intArrayOf(128, 64, 32, 16).forEach{
            addIcon("libgdx${"$"}it.png", Files.FileType.Internal)
        }
    })
}
"""

  override fun getAndroidLauncherContent(project: Project): String = """package ${project.basic.rootPackage}.android

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import ${project.basic.rootPackage}.${project.basic.mainClass}

/** Launches the Android application. */
class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(${project.basic.mainClass}(), AndroidApplicationConfiguration().apply {
            // Configure your application here.
            useImmersiveMode = true // Recommended, but not required.
        })
    }
}
"""

  override fun getHeadlessLauncherContent(project: Project): String = """@file:JvmName("HeadlessLauncher")

package ${project.basic.rootPackage}.headless

import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration
import ${project.basic.rootPackage}.${project.basic.mainClass}

/** Launches the headless application. Can be converted into a server application or a scripting utility. */
fun main() {
    HeadlessApplication(${project.basic.mainClass}(), HeadlessApplicationConfiguration().apply {
        // When this value is negative, ${project.basic.mainClass}#render() is never called:
        updatesPerSecond = -1
    })
}
"""

  override fun getLwjgl3LauncherContent(project: Project): String = """@file:JvmName("Lwjgl3Launcher")

package ${project.basic.rootPackage}.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import ${project.basic.rootPackage}.${project.basic.mainClass}

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
      return
    Lwjgl3Application(${project.basic.mainClass}(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("${project.basic.name}")
        setWindowedMode($width, $height)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx${"$"}it.png" }.toTypedArray()))
    })
}
"""

  override fun getLwjgl3StartupContent(project: Project): String {
    return """
/*
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
package ${project.basic.rootPackage}.lwjgl3

import org.lwjgl.system.macosx.LibC
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.management.ManagementFactory
import java.util.*

/**
 * Adds some utilities to ensure that the JVM was started with the
 * `-XstartOnFirstThread` argument, which is required on macOS for LWJGL 3
 * to function. Also helps on Windows when users have names with characters from
 * outside the Latin alphabet, a common cause of startup crashes.
 *
 * [Based on this java-gaming.org post by kappa](https://jvm-gaming.org/t/starting-jvm-on-mac-with-xstartonfirstthread-programmatically/57547)
 * @author damios
 */
class StartupHelper private constructor() {
    init {
        throw UnsupportedOperationException()
    }

    companion object {
        private const val JVM_RESTARTED_ARG = "jvmIsRestarted"
        /**
         * Starts a new JVM if the application was started on macOS without the
         * `-XstartOnFirstThread` argument. This also includes some code for
         * Windows, for the case where the user's home directory includes certain
         * non-Latin-alphabet characters (without this code, most LWJGL3 apps fail
         * immediately for those users). Returns whether a new JVM was started and
         * thus no code should be executed.
         *
         * **Usage:**
         *
         * ```
         * fun main() {
         *   if (StartupHelper.startNewJvmIfRequired(true)) return // This handles macOS support and helps on Windows.
         *   // after this is the actual main method code
         * }
         * ```
         *
         * @param redirectOutput
         * whether the output of the new JVM should be rerouted to the
         * old JVM, so it can be accessed in the same place; keeps the
         * old JVM running if enabled
         * @return whether a new JVM was started and thus no code should be executed
         * in this one
         */
        @JvmOverloads
        fun startNewJvmIfRequired(redirectOutput: Boolean = true): Boolean {
            val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
            if (!osName.contains("mac")) {
                if (osName.contains("windows")) {
// Here, we are trying to work around an issue with how LWJGL3 loads its extracted .dll files.
// By default, LWJGL3 extracts to the directory specified by "java.io.tmpdir", which is usually the user's home.
// If the user's name has non-ASCII (or some non-alphanumeric) characters in it, that would fail.
// By extracting to the relevant "ProgramData" folder, which is usually "C:\ProgramData", we avoid this.
                    System.setProperty("java.io.tmpdir", System.getenv("ProgramData") + "/libGDX-temp")
                }
                return false
            }

            // There is no need for -XstartOnFirstThread on Graal native image
            if (System.getProperty("org.graalvm.nativeimage.imagecode", "").isNotEmpty()) {
                return false
            }

            val pid = LibC.getpid()

            // check whether -XstartOnFirstThread is enabled
            if ("1" == System.getenv("JAVA_STARTED_ON_FIRST_THREAD_${"$"}pid")) {
                return false
            }

            // check whether the JVM was previously restarted
            // avoids looping, but most certainly leads to a crash
            if ("true" == System.getProperty(JVM_RESTARTED_ARG)) {
                System.err.println(
                    "There was a problem evaluating whether the JVM was started with the -XstartOnFirstThread argument."
                )
                return false
            }

            // Restart the JVM with -XstartOnFirstThread
            val jvmArgs = ArrayList<String?>()
            val separator = System.getProperty("file.separator")
            // The following line is used assuming you target Java 8, the minimum for LWJGL3.
            val javaExecPath = System.getProperty("java.home") + separator + "bin" + separator + "java"
            // If targeting Java 9 or higher, you could use the following instead of the above line:
            //String javaExecPath = ProcessHandle.current().info().command().orElseThrow();
            if (!File(javaExecPath).exists()) {
                System.err.println(
                    "A Java installation could not be found. If you are distributing this app with a bundled JRE, be sure to set the -XstartOnFirstThread argument manually!"
                )
                return false
            }
            jvmArgs.add(javaExecPath)
            jvmArgs.add("-XstartOnFirstThread")
            jvmArgs.add("-D${"$"}JVM_RESTARTED_ARG=true")
            jvmArgs.addAll(ManagementFactory.getRuntimeMXBean().inputArguments)
            jvmArgs.add("-cp")
            jvmArgs.add(System.getProperty("java.class.path"))
            var mainClass = System.getenv("JAVA_MAIN_CLASS_${"$"}pid")
            if (mainClass == null) {
                val trace = Thread.currentThread().stackTrace
                mainClass = if (trace.isNotEmpty()) {
                    trace[trace.size - 1].className
                } else {
                    System.err.println("The main class could not be determined.")
                    return false
                }
            }
            jvmArgs.add(mainClass)
            try {
                if (!redirectOutput) {
                    val processBuilder = ProcessBuilder(jvmArgs)
                    processBuilder.start()
                } else {
                    val process = ProcessBuilder(jvmArgs)
                        .redirectErrorStream(true).start()
                    val processOutput = BufferedReader(
                        InputStreamReader(process.inputStream)
                    )
                    var line: String?
                    while (processOutput.readLine().also { line = it } != null) {
                        println(line)
                    }
                    process.waitFor()
                }
            } catch (e: Exception) {
                System.err.println("There was a problem restarting the JVM")
                e.printStackTrace()
            }
            return true
        }
    }
}
"""
  }

  override fun getIOSLauncherContent(project: Project): String = """@file:JvmName("IOSLauncher")

package ${project.basic.rootPackage}.ios

import org.robovm.apple.foundation.NSAutoreleasePool
import org.robovm.apple.uikit.UIApplication

import com.badlogic.gdx.backends.iosrobovm.IOSApplication
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration
import ${project.basic.rootPackage}.${project.basic.mainClass}

/** Launches the iOS (RoboVM) application. */
class IOSLauncher : IOSApplication.Delegate() {
    override fun createApplication(): IOSApplication {
        return IOSApplication(${project.basic.mainClass}(), IOSApplicationConfiguration().apply {
            // Configure your application here.
        })
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val pool = NSAutoreleasePool()
            val principalClass: Class<UIApplication>? = null
            val delegateClass = IOSLauncher::class.java
            UIApplication.main(args, principalClass, delegateClass)
            pool.close()
        }
    }
}"""

  override fun getGwtLauncherContent(project: Project): String = """package ${project.basic.rootPackage}.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.utils.GdxRuntimeException;
// import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig () {
        return new GwtApplicationConfiguration($width, $height);
    }

    @Override
    public ApplicationListener createApplicationListener () {
        throw new GdxRuntimeException("Kotlin is currently not supported by GWT.");
        // return new ${project.basic.mainClass}();
    }
}
"""

  override fun getServerLauncherContent(project: Project) = """@file:JvmName("ServerLauncher")

package ${project.basic.rootPackage}.server

/** Launches the server application. */
fun main() {
    TODO("Implement server application.")
}
"""

  override fun getTeaVMLauncherContent(project: Project): String = """@file:JvmName("TeaVMLauncher")

package ${project.basic.rootPackage}.teavm

import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration
import com.github.xpenatan.gdx.backends.teavm.TeaApplication
import ${project.basic.rootPackage}.${project.basic.mainClass}

/** Launches the TeaVM/HTML application. */
fun main() {
    val config = TeaApplicationConfiguration("canvas").apply {
        width = $width
        height = $height
    }
    TeaApplication(${project.basic.mainClass}(), config)
}
"""

  override fun getTeaVMBuilderContent(project: Project) = """package ${project.basic.rootPackage}.teavm

import java.io.File
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder
import com.github.xpenatan.gdx.backends.teavm.config.plugins.TeaReflectionSupplier
import com.github.xpenatan.gdx.backends.teavm.gen.SkipClass

/** Builds the TeaVM/HTML application. */
@SkipClass
object TeaVMBuilder {
    @JvmStatic fun main(arguments: Array<String>) {
        val teaBuildConfiguration = TeaBuildConfiguration().apply {
            assetsPath.add(File("../assets"))
            webappPath = File("build/dist").canonicalPath
            // Register any extra classpath assets here:
            // additionalAssetsClasspathFiles += "${project.basic.rootPackage.replace('.', '/')}/asset.extension"
        }

        // Register any classes or packages that require reflection here:
${generateTeaVMReflectionIncludes(project, indent = " ".repeat(8), trailingSemicolon = false)}

        val tool = TeaBuilder.config(teaBuildConfiguration)
        tool.mainClass = "${project.basic.rootPackage}.teavm.TeaVMLauncher"
        TeaBuilder.build(tool)
    }
}
"""
}
