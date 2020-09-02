package com.github.czyzby.setup

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.github.czyzby.autumn.context.ContextInitializer
import com.github.czyzby.autumn.fcs.scanner.DesktopClassScanner
import com.github.czyzby.autumn.mvc.application.AutumnApplication
import com.github.czyzby.setup.config.Configuration
import com.github.czyzby.setup.views.Extension
import com.github.czyzby.setup.views.GdxPlatform
import com.github.czyzby.setup.views.JvmLanguage
import com.github.czyzby.setup.views.ProjectTemplate
import com.kotcrab.vis.ui.util.OsUtils

fun main(args: Array<String>) {
    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("gdx-liftoff")
    config.setWindowedMode(Configuration.WIDTH, Configuration.HEIGHT)
    config.disableAudio(true)
//    config.setDecorated(false)
    config.setResizable(false)
    config.setWindowIcon(*arrayOf(128, 64, 32, 16).map { "icons/libgdx$it.png" }.toTypedArray())

    try {
        Lwjgl3Application(object : AutumnApplication(DesktopClassScanner(), Root::class.java) {
            override fun registerDefaultComponentAnnotations(initializer: ContextInitializer) {
                super.registerDefaultComponentAnnotations(initializer)
                initializer.scanFor(Extension::class.java, ProjectTemplate::class.java, JvmLanguage::class.java,
                        GdxPlatform::class.java)
            }
        }, config)
    } catch(error: ExceptionInInitializerError) {
        if (OsUtils.isMac() && error.cause is IllegalStateException) {
            if (error.stackTraceToString().contains("XstartOnFirstThread")) {
                println("Application was not launched on first thread. Restarting with -XstartOnFirstThread. " +
                        "Add VM argument -XstartOnFirstThread to avoid this.")
                Application.startNewInstance()
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
