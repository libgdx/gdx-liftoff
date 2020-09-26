package com.github.czyzby.setup

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils
import com.github.czyzby.autumn.context.ContextInitializer
import com.github.czyzby.autumn.fcs.scanner.DesktopClassScanner
import com.github.czyzby.autumn.mvc.application.AutumnApplication
import com.github.czyzby.setup.config.Configuration
import com.github.czyzby.setup.views.Extension
import com.github.czyzby.setup.views.GdxPlatform
import com.github.czyzby.setup.views.JvmLanguage
import com.github.czyzby.setup.views.ProjectTemplate
import com.kotcrab.vis.ui.util.OsUtils
import org.lwjgl.system.macosx.LibC
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.management.ManagementFactory
import java.util.*

private const val JVM_RESTARTED_ARG = "jvmIsRestarted"

/**
 * Calling this should be done in an if check; if this returns true, the old program should end because a new JVM will
 * take over, but if it returns false, the program should continue normally. It is meant to allow MacOS to start with
 * its required '-XstartOnFirstThread' argument, even if the jar wasn't originally started with it.
 * Taken from https://github.com/crykn/guacamole/blob/master/gdx-desktop/src/main/java/de/damios/guacamole/gdx/StartOnFirstThreadHelper.java .
 * Thanks crykn/damios!
 */
fun startNewJvmIfRequired(): Boolean {
    if (!UIUtils.isMac) {
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
                "There was a problem evaluating whether the JVM was started with the -XstartOnFirstThread argument")
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
    jvmArgs.add(System.getenv("JAVA_MAIN_CLASS_$pid"))
    try {
        val process = ProcessBuilder(jvmArgs).redirectErrorStream(true).start()
        val processOutput = BufferedReader(InputStreamReader(process.inputStream))
        var line: String?
        while (processOutput.readLine().also { line = it } != null) {
            println(line)
        }
        process.waitFor()
    } catch (e: Exception) {
        System.err.println("There was a problem restarting the JVM")
        e.printStackTrace()
    }
    return true
}

fun main() {
    if(startNewJvmIfRequired()) return
    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("gdx-liftoff")
    config.setWindowedMode(Configuration.WIDTH, Configuration.HEIGHT)
    config.disableAudio(true)
//    config.setDecorated(false)
    config.setResizable(true)
    config.setForegroundFPS(16)
    config.setIdleFPS(8)
    config.setWindowIcon(*arrayOf(128, 64, 32, 16).map { "icons/libgdx$it.png" }.toTypedArray())

    try {
        Lwjgl3Application(object : AutumnApplication(DesktopClassScanner(), Root::class.java) {
            override fun registerDefaultComponentAnnotations(initializer: ContextInitializer) {
                super.registerDefaultComponentAnnotations(initializer)
                initializer.scanFor(Extension::class.java, ProjectTemplate::class.java, JvmLanguage::class.java,
                        GdxPlatform::class.java)
            }
        }, config)
    } catch (error: ExceptionInInitializerError) {
        if (OsUtils.isMac() && error.cause is IllegalStateException) {
            if (error.stackTraceToString().contains("XstartOnFirstThread")) {
                println("Application was not launched on first thread. " +
                        "Add VM argument -XstartOnFirstThread to avoid this.")
            }
        }         
        throw error
    }
}

/**
 * Application's scanning root.
 * @author MJ
 */
class Root
