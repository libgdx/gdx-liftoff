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

  override fun getLwjgl2LauncherContent(project: Project): String =
    """@file:JvmName("Lwjgl2Launcher")

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

  override fun getAndroidLauncherContent(project: Project): String =
    """package ${project.basic.rootPackage}.android

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

  override fun getHeadlessLauncherContent(project: Project): String =
    """@file:JvmName("HeadlessLauncher")

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

  override fun getIOSMOELauncherContent(project: Project): String =
    """package ${project.basic.rootPackage}

import apple.uikit.c.UIKit
import com.badlogic.gdx.backends.iosmoe.IOSApplication
import com.badlogic.gdx.backends.iosmoe.IOSApplicationConfiguration
import org.moe.natj.general.Pointer
import org.moe.natj.general.ptr.BytePtr
import org.moe.natj.general.ptr.Ptr
import org.moe.natj.general.ptr.impl.PtrFactory
import ${project.basic.rootPackage}.${project.basic.mainClass}

/** Launches the iOS (Multi-Os Engine) application. */
class IOSLauncher(peer: Pointer) : IOSApplication.Delegate(peer) {
    override fun createApplication(): IOSApplication {
        return IOSApplication(${project.basic.mainClass}(), IOSApplicationConfiguration().apply {
            // Configure your application here.
        })
    }
}

fun main() {
    // This is an ugly call into native code because UIKit isn't a typical Java API.
    // It only is uglier because it needs PtrFactory to create an empty pointer to some bytes...
    // And that is only needed because Kotlin won't tolerate null there, even if it isn't used.
    @Suppress("UNCHECKED_CAST")
    UIKit.UIApplicationMain(0,
        PtrFactory.newPointerPtr(Byte::class.java, 2, 0, true, true) as Ptr<BytePtr>,
        null, IOSLauncher::class.java.name)
}"""

  override fun getIOSMOESVMRegistrationContent(project: Project): String =
    """package ${project.basic.rootPackage}

import org.graalvm.nativeimage.hosted.Feature
import org.graalvm.nativeimage.hosted.RuntimeJNIAccess

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.DoubleBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.LongBuffer
import java.nio.ShortBuffer
/**
 * Registers the reflection and JNI access done by libGDX by default.
 * The relevant documentation is here:
 * for [Feature](https://www.graalvm.org/sdk/javadoc/org/graalvm/nativeimage/hosted/Feature.html),
 * and for [metadata in general](https://www.graalvm.org/latest/reference-manual/native-image/metadata).
 * This class may need to be modified if you use additional JNI or reflective access.
 */
class SVMRegistrationFeature : Feature {

    override fun beforeAnalysis(access : Feature.BeforeAnalysisAccess) {
        RuntimeJNIAccess.register(String::class.java)
        RuntimeJNIAccess.register(DoubleBuffer::class.java, IntBuffer::class.java,
            FloatBuffer::class.java, Buffer::class.java, LongBuffer::class.java,
            CharBuffer::class.java, ByteBuffer::class.java, ShortBuffer::class.java)
    }
}"""

  override fun getLwjgl3LauncherContent(project: Project): String =
    """@file:JvmName("Lwjgl3Launcher")

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
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        useVsync(true)
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1)
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.


        setWindowedMode($width, $height)
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx${"$"}it.png" }.toTypedArray()))

        //// This could improve compatibility with Windows machines with buggy OpenGL drivers, Macs
        //// with Apple Silicon that have to emulate compatibility with OpenGL anyway, and more.
        //// This uses the dependency `com.badlogicgames.gdx:gdx-lwjgl3-angle` to function.
        //// You would need to add this line to lwjgl3/build.gradle , below the dependency on `gdx-backend-lwjgl3`:
        ////     implementation "com.badlogicgames.gdx:gdx-lwjgl3-angle:${'$'}gdxVersion"
        //// You can choose to add the following line and the mentioned dependency if you want; they
        //// are not intended for games that use GL30 (which is compatibility with OpenGL ES 3.0).
        //// Know that it might not work well in some cases.
//        setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0)

    })
}
"""

  override fun getLwjgl3StartupContent(project: Project): String =
    """
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

import com.badlogic.gdx.Version
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader
import org.lwjgl.system.JNI
import org.lwjgl.system.macosx.LibC
import org.lwjgl.system.macosx.ObjCRuntime
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
         * Must only be called on Linux. Check OS first!
         * @return true if NVIDIA drivers are in use on Linux, false otherwise
         */
        fun isLinuxNvidia() = !File("/proc/driver").list { _, path: String -> "NVIDIA" in path.uppercase() }.isNullOrEmpty()


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
            val osName = System.getProperty("os.name").lowercase(Locale.ROOT)
            if (!osName.contains("mac")) {
                if (osName.contains("windows")) {
                  // Here, we are trying to work around an issue with how LWJGL3 loads its extracted .dll files.
                  // By default, LWJGL3 extracts to the directory specified by "java.io.tmpdir", which is usually the user's home.
                  // If the user's name has non-ASCII (or some non-alphanumeric) characters in it, that would fail.
                  // By extracting to the relevant "ProgramData" folder, which is usually "C:\ProgramData", we avoid this.
                  // We also temporarily change the "user.name" property to one without any chars that would be invalid.
                  // We revert our changes immediately after loading LWJGL3 natives.
                  val programData = System.getenv("ProgramData") ?: "C:\\Temp\\"
                  val prevTmpDir = System.getProperty("java.io.tmpdir", programData)
                  val prevUser = System.getProperty("user.name", "libGDX_User")
                  System.setProperty("java.io.tmpdir", "${'$'}programData/libGDX-temp")
                  System.setProperty("user.name", "User_${'$'}{prevUser.hashCode()}_GDX${'$'}{Version.VERSION}".replace('.', '_'))
                  Lwjgl3NativesLoader.load()
                  System.setProperty("java.io.tmpdir", prevTmpDir)
                  System.setProperty("user.name", prevUser)
                } else {
                    // not Mac or Windows, assuming Linux
                    if(isLinuxNvidia()) {
                        // check whether __GL_THREADED_OPTIMIZATIONS is already disabled
                        if ("0" == System.getenv("__GL_THREADED_OPTIMIZATIONS")) {
                            return false
                        }

                        // check whether the JVM was previously restarted
                        // avoids looping, but most certainly leads to a crash
                        if ("true" == System.getProperty(JVM_RESTARTED_ARG)) {
                            System.err.println(
                                "There was a problem evaluating whether the JVM was restarted with __GL_THREADED_OPTIMIZATIONS disabled.")
                            return false
                        }

                        // Restart the JVM with __GL_THREADED_OPTIMIZATIONS disabled
                        val jvmArgs = ArrayList<String?>()
                        val separator = System.getProperty("file.separator", "/")
                        // The following line is used assuming you target Java 8, the minimum for LWJGL3.
                        val javaExecPath = System.getProperty("java.home") + separator + "bin" + separator + "java"
                        // If targeting Java 9 or higher, you could use the following instead of the above line:
                        //val javaExecPath = ProcessHandle.current().info().command().orElseThrow()
                        if (!File(javaExecPath).exists()) {
                            System.err.println(
                                "A Java installation could not be found. If you are distributing this app with a bundled JRE, be sure to set the environment variable __GL_THREADED_OPTIMIZATIONS=0"
                            )
                            return false
                        }

                        jvmArgs.add(javaExecPath)
                        jvmArgs.add("-D${"$"}JVM_RESTARTED_ARG=true")
                        jvmArgs.addAll(ManagementFactory.getRuntimeMXBean().inputArguments)
                        jvmArgs.add("-cp")
                        jvmArgs.add(System.getProperty("java.class.path"))
                        var mainClass = System.getenv("JAVA_MAIN_CLASS_${"$"}{org.lwjgl.system.linux.UNISTD.getpid()}")
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
                                processBuilder.environment()["__GL_THREADED_OPTIMIZATIONS"] = "0"
                                processBuilder.start()
                            } else {
                                val processBuilder = ProcessBuilder(jvmArgs)
                                processBuilder.environment()["__GL_THREADED_OPTIMIZATIONS"] = "0"
                                val process = processBuilder.redirectErrorStream(true).start()
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
                return false
            }

            // There is no need for -XstartOnFirstThread on Graal native image
            if (System.getProperty("org.graalvm.nativeimage.imagecode", "").isNotEmpty()) {
                return false
            }

            // Checks if we are already on the main thread, such as from running via Construo.
            val objcMsgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend")
            val nsThread = ObjCRuntime.objc_getClass("NSThread")
            val currentThread = JNI.invokePPP(nsThread, ObjCRuntime.sel_getUid("currentThread"), objcMsgSend)
            val isMainThread = JNI.invokePPZ(currentThread, ObjCRuntime.sel_getUid("isMainThread"), objcMsgSend)
            if (isMainThread) return false

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
            val separator = System.getProperty("file.separator", "/")
            // The following line is used assuming you target Java 8, the minimum for LWJGL3.
            val javaExecPath = System.getProperty("java.home") + separator + "bin" + separator + "java"
            // If targeting Java 9 or higher, you could use the following instead of the above line:
            //val javaExecPath = ProcessHandle.current().info().command().orElseThrow()
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

  override fun getIOSLauncherContent(project: Project): String =
    """@file:JvmName("IOSLauncher")

package ${project.basic.rootPackage}

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

  override fun getGwtLauncherContent(project: Project): String =
    """package ${project.basic.rootPackage}.gwt;

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

  override fun getServerLauncherContent(project: Project) =
    """@file:JvmName("ServerLauncher")

package ${project.basic.rootPackage}.server

/** Launches the server application. */
fun main() {
    TODO("Implement server application.")
}
"""

  override fun getTeaVMLauncherContent(project: Project): String =
    """@file:JvmName("TeaVMLauncher")

package ${project.basic.rootPackage}.teavm

import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration
import com.github.xpenatan.gdx.backends.teavm.TeaApplication
import ${project.basic.rootPackage}.${project.basic.mainClass}

/** Launches the TeaVM/HTML application. */
fun main() {
    val config = TeaApplicationConfiguration("canvas").apply {
        //// If width and height are each greater than 0, then the app will use a fixed size.
        //width = $width
        //height = $height
        //// If width and height are both 0, then the app will use all available space.
        width = 0
        height = 0
    }
    TeaApplication(${project.basic.mainClass}(), config)
}
"""

  override fun getTeaVMBuilderContent(project: Project) =
    """package ${project.basic.rootPackage}.teavm

import java.io.File
import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildReflectionListener
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder
import com.github.xpenatan.gdx.backends.teavm.config.plugins.TeaReflectionSupplier
import org.teavm.backend.wasm.WasmDebugInfoLevel
import org.teavm.tooling.TeaVMSourceFilePolicy
import org.teavm.tooling.TeaVMTargetType
import org.teavm.tooling.TeaVMTool
import org.teavm.tooling.sources.DirectorySourceFileProvider
import org.teavm.vm.TeaVMOptimizationLevel

/** Builds the TeaVM/HTML application. */
object TeaVMBuilder {
    /**
     * A single point to configure most debug vs. release settings.
     * This defaults to false in new projects; set this to false when you want to release.
     * If this is true, the output will not be obfuscated, and debug information will usually be produced.
     * You can still set obfuscation to false in a release if you want the source to be at least a little legible.
     * This works well when the targetType is set to JAVASCRIPT, but you can still set the targetType to WEBASSEMBLY_GC
     * while this is true in order to test that higher-performance target before releasing.
     */
    private const val DEBUG = true

    @JvmStatic fun main(arguments: Array<String>) {
        val teaBuildConfiguration = TeaBuildConfiguration().apply {
            assetsPath.add(AssetFileHandle("../assets"))
            webappPath = File("build/dist").canonicalPath
            // Register any extra classpath assets here:
//            additionalAssetsClasspathFiles += "${project.basic.rootPackage.replace('.', '/')}/asset.extension"
        }

        // If you need to match specific classes based on the package and class name,
        // you can use the reflectionListener to do fine-grained matching on the String fullClassName.
//        teaBuildConfiguration.reflectionListener = TeaBuildReflectionListener { fullClassName: String? ->
//            if (fullClassName!!.startsWith("where.your.reflective.code.is") &&
//                fullClassName.endsWith("YourSuffix"))
//                return@TeaBuildReflectionListener true
//            false
//        }

        // You can also register any classes or packages that require reflection here:
${generateTeaVMReflectionIncludes(project, indent = " ".repeat(8), trailingSemicolon = false)}

        // JavaScript is the default target type for TeaVM, and it works better during debugging.
        teaBuildConfiguration.targetType = TeaVMTargetType.JAVASCRIPT
        // You can choose to use the WebAssembly (WASM) GC target instead, which tends to perform better, but isn't
        // as easy to debug. It might be a good idea to alternate target types during development if you plan on using
        // WASM at release time.
//        teaBuildConfiguration.targetType = TeaVMTargetType.WEBASSEMBLY_GC

        TeaBuilder.config(teaBuildConfiguration)
        val tool = TeaVMTool()

        tool.mainClass = "${project.basic.rootPackage}.teavm.TeaVMLauncher"
        // For many (or most) applications, using a high optimization won't add much to build time.
        // If your builds take too long, and runtime performance doesn't matter, you can change ADVANCED to SIMPLE .
        // Using SIMPLE makes debugging easier, also, so it is used when DEBUG is enabled.
        tool.optimizationLevel = if(DEBUG) TeaVMOptimizationLevel.SIMPLE else TeaVMOptimizationLevel.ADVANCED
        // The line below will make the generated code hard to read (and smaller) in releases and easier to follow
        // when DEBUG is true. Setting DEBUG to false should always be done before a release, anyway.
        tool.setObfuscated(!DEBUG)

        // If DEBUG is set to true, these lines allow step-debugging JVM languages from the browser,
        // setting breakpoints in Java code and stopping in the appropriate place in generated browser code.
        // This may work reasonably well when targeting WEBASSEMBLY_GC, but it usually works better with JAVASCRIPT .
        if(DEBUG) {
            tool.isDebugInformationGenerated = true
            tool.isSourceMapsFileGenerated = true
            tool.setWasmDebugInfoLevel(WasmDebugInfoLevel.FULL)
            tool.setSourceFilePolicy(TeaVMSourceFilePolicy.COPY)
            tool.addSourceFileProvider(DirectorySourceFileProvider(File("../core/src/main/kotlin/")))
        }

        TeaBuilder.build(tool)
    }
}
"""
}
