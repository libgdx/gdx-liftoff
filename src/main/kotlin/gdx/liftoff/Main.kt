package gdx.liftoff

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils
import com.badlogic.gdx.utils.GdxRuntimeException
import com.github.czyzby.autumn.context.ContextInitializer
import com.github.czyzby.autumn.fcs.scanner.DesktopClassScanner
import com.github.czyzby.autumn.mvc.application.AutumnApplication
import com.github.czyzby.autumn.nongwt.scanner.FallbackDesktopClassScanner
import com.kotcrab.vis.ui.util.OsUtils
import gdx.liftoff.config.Configuration
import gdx.liftoff.views.Extension
import gdx.liftoff.views.GdxPlatform
import gdx.liftoff.views.JvmLanguage
import gdx.liftoff.views.ProjectTemplate
import org.lwjgl.system.macosx.LibC
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.management.ManagementFactory

private const val JVM_RESTARTED_ARG = "jvmIsRestarted"

/**
 * Calling this should be done in an if check; if this returns true, the old program should end because a new JVM will
 * take over, but if it returns false, the program should continue normally. It is meant to allow macOS to start with
 * its required '-XstartOnFirstThread' argument, even if the jar wasn't originally started with it.
 * Taken from https://github.com/crykn/guacamole/blob/master/gdx-desktop/src/main/java/de/damios/guacamole/gdx/StartOnFirstThreadHelper.java .
 * Thanks crykn/damios!
 */
fun startNewJvmIfRequired(): Boolean {
  if (!UIUtils.isMac) {
    if (UIUtils.isWindows) {
      // Here, we are trying to work around an issue with how LWJGL3 loads its extracted .dll files.
      // By default, LWJGL3 extracts to the directory specified by "java.io.tmpdir", which is usually the user's home.
      // If the user's name has non-ASCII (or some non-alphanumeric) characters in it, that would fail.
      // By extracting to the relevant "ProgramData" folder, which is usually "C:\ProgramData", we avoid this.
      System.setProperty("java.io.tmpdir", "${System.getenv("ProgramData")}/libGDX-temp")
    }
    return false
  }

  // There is no need for -XstartOnFirstThread on Graal native image
  if (System.getProperty("org.graalvm.nativeimage.imagecode", "").isNotEmpty()) {
    return false
  }

  val pid = LibC.getpid()

  // check whether -XstartOnFirstThread is enabled
  if ("1" == System.getenv("JAVA_STARTED_ON_FIRST_THREAD_$pid")) {
    return false
  }

  // check whether the JVM was previously restarted
  // avoids looping, but most certainly leads to a crash
  if ("true" == System.getProperty(JVM_RESTARTED_ARG)) {
    System.err.println(
      "There was a problem evaluating whether the JVM was started with the -XstartOnFirstThread argument"
    )
    return false
  }

  // Restart the JVM with -XstartOnFirstThread
  val jvmArgs = ArrayList<String>()
  val separator = System.getProperty("file.separator")
  jvmArgs.add(System.getProperty("java.home") + separator + "bin" + separator + "java")
  jvmArgs.add("-XstartOnFirstThread")
  jvmArgs.add("-D$JVM_RESTARTED_ARG=true")
  jvmArgs.addAll(ManagementFactory.getRuntimeMXBean().inputArguments)
  jvmArgs.add("-cp")
  jvmArgs.add(System.getProperty("java.class.path"))
  var mainClass = System.getenv("JAVA_MAIN_CLASS_$pid")
  if (mainClass == null) {
    val trace = Thread.currentThread().stackTrace
    mainClass = if (trace.isNotEmpty()) {
      trace[trace.size - 1].className
    } else {
      System.err.println("The main class could not be determined.")
      return false
    }
  }
  jvmArgs.add(mainClass!!)
  try {
    // // Used if we need to print to the console, but might not always close correctly.
    val process = ProcessBuilder(jvmArgs).redirectErrorStream(true).start()
    val processOutput = BufferedReader(InputStreamReader(process.inputStream))
    var line: String?
    while (processOutput.readLine().also { line = it } != null) {
      println(line)
    }
    process.waitFor()
    // // Used if we don't want to make a new JVM, but doesn't seem to always close correctly, either...
//    val processBuilder = ProcessBuilder(jvmArgs)
//    processBuilder.start()
  } catch (e: Exception) {
    System.err.println("There was a problem restarting the JVM")
    e.printStackTrace()
  }
  return true
}

fun main() {
  if (startNewJvmIfRequired()) return
  val config = Lwjgl3ApplicationConfiguration()
  config.setTitle("gdx-liftoff")
  config.setWindowedMode(Configuration.WIDTH, Configuration.HEIGHT)
  config.disableAudio(true)
  config.setResizable(true)
  config.setForegroundFPS(16)
  config.setIdleFPS(8)
  config.setAutoIconify(true)
  config.setWindowIcon(*arrayOf(128, 64, 32, 16).map { "icons/libgdx$it.png" }.toTypedArray())
  val windowListener: Lwjgl3WindowListener = object : Lwjgl3WindowListener {
    override fun focusLost() { Gdx.graphics.isContinuousRendering = false }
    override fun focusGained() { Gdx.graphics.isContinuousRendering = true }
    override fun created(window: Lwjgl3Window) {}
    override fun iconified(isIconified: Boolean) {}
    override fun maximized(isMaximized: Boolean) {}
    override fun closeRequested(): Boolean { return true }
    override fun filesDropped(files: Array<String>) {}
    override fun refreshRequested() {}
  }
  config.setWindowListener(windowListener)

  try {
    Lwjgl3Application(
      object : AutumnApplication(DesktopClassScanner(), Root::class.java) {
        override fun registerDefaultComponentAnnotations(initializer: ContextInitializer) {
          super.registerDefaultComponentAnnotations(initializer)
          // Classes with these annotations will be automatically scanned for and initiated as singletons:
          initializer.scanFor(
            Extension::class.java,
            ProjectTemplate::class.java,
            JvmLanguage::class.java,
            GdxPlatform::class.java
          )
        }
      },
      config
    )
  } catch (error: ExceptionInInitializerError) {
    if (OsUtils.isMac() && error.cause is IllegalStateException) {
      if (error.stackTraceToString().contains("XstartOnFirstThread")) {
        println(
          "Application was not launched on first thread. " +
            "Add VM argument -XstartOnFirstThread to avoid this."
        )
      }
    }
    throw error
  } catch (error: GdxRuntimeException) {
    Lwjgl3Application(
      object : AutumnApplication(FallbackDesktopClassScanner(), Root::class.java) {
        override fun registerDefaultComponentAnnotations(initializer: ContextInitializer) {
          super.registerDefaultComponentAnnotations(initializer)
          // Classes with these annotations will be automatically scanned for and initiated as singletons:
          initializer.scanFor(
            Extension::class.java,
            ProjectTemplate::class.java,
            JvmLanguage::class.java,
            GdxPlatform::class.java
          )
        }
      },
      config
    )
  }
}

fun Throwable.stackTraceToString(): String {
  val stringWriter = StringWriter()
  val printWriter = PrintWriter(stringWriter, true)
  printStackTrace(printWriter)
  return stringWriter.buffer.toString()
}

/**
 * Application's scanning root for Autumn dependency injection framework.
 */
class Root
