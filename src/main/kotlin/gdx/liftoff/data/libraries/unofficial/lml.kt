@file:Suppress("unused") // Extension classes accessed via reflection.

package gdx.liftoff.data.libraries.unofficial

import gdx.liftoff.data.libraries.Repository
import gdx.liftoff.data.libraries.camelCaseToKebabCase
import gdx.liftoff.data.platforms.Android
import gdx.liftoff.data.platforms.Core
import gdx.liftoff.data.platforms.GWT
import gdx.liftoff.data.platforms.Headless
import gdx.liftoff.data.platforms.Server
import gdx.liftoff.data.platforms.Shared
import gdx.liftoff.data.platforms.iOS
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.Extension

/**
 * Various utilities for libGDX APIs including GUI building and dependency injection.
 * @author czyzby
 * @author metaphore (CrashInvaders)
 */
abstract class LmlExtension : ThirdPartyExtension() {
    /**
     * Latest version of gdx-lml libraries from the CrashInvaders fork.
     */
    override val defaultVersion = "1.9.1.10.0"
    override val repository = Repository.OTHER
    override val group = "com.crashinvaders.lml"
    override val name: String
        get() = "gdx-" + id.camelCaseToKebabCase()
}

/**
 * Guava-inspired libGDX utilities; no longer maintained.
 * Kiwi is actually used by gdx-liftoff and you can pull an updated fork of Kiwi code from the "com.github.czyzby.kiwi"
 * Java package in gdx-liftoff's repo.
 */
@Extension
class Kiwi : LmlExtension() {
    override val id = "kiwi"
    override val url = "https://github.com/crashinvaders/gdx-lml/tree/master/kiwi"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "$group:$name")

        addDependency(project, GWT.ID, "$group:$name:sources")
        addGwtInherit(project, "$group.kiwi.GdxKiwi")
    }
}

/**
 * Parser of HTML-like templates that produces Scene2D widgets; no longer maintained.
 * LML is actually used by gdx-liftoff and you can pull an updated fork of LML code from the "com.github.czyzby.lml"
 * Java package in gdx-liftoff's repo.
 */
@Extension
class LML : LmlExtension() {
    override val id = "lml"
    override val url = "https://github.com/crashinvaders/gdx-lml/tree/master/lml"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "$group:$name")

        addDependency(project, GWT.ID, "$group:$name:sources")
        addGwtInherit(project, "$group.lml.GdxLml")

        Kiwi().initiate(project)
    }
}

/**
 * Parser of HTML-like templates that produces VisUI widgets; no longer maintained.
 * LMLVis is actually used by gdx-liftoff and you can pull an updated fork of LMLVis code from the
 * "com.github.czyzby.lml" Java package in gdx-liftoff's repo.
 */
@Extension
class LMLVis : LmlExtension() {
    override val id = "lmlVis"
    override val url = "https://github.com/crashinvaders/gdx-lml/tree/master/lml-vis"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "$group:gdx-lml-vis")

        addDependency(project, GWT.ID, "$group:gdx-lml-vis:sources")
        addGwtInherit(project, "$group.lml.vis.GdxLmlVis")

        LML().initiate(project)
        VisUI().initiate(project)
    }
}

/**
 * Dependency injection mechanism with component scan using libGDX reflection API; no longer maintained.
 * Autumn is actually used by gdx-liftoff and you can pull an updated fork of Autumn code from the
 * "com.github.czyzby.autumn" Java package in gdx-liftoff's repo.
 */
@Extension
class Autumn : LmlExtension() {
    override val id = "autumn"
    override val url = "https://github.com/crashinvaders/gdx-lml/tree/master/autumn"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "$group:gdx-autumn")

        addDesktopDependency(project, "$group:gdx-autumn-fcs")

        addDependency(project, Headless.ID, "$group:gdx-autumn-fcs")

        addDependency(project, Android.ID, "$group:gdx-autumn-android")

        addDependency(project, GWT.ID, "$group:gdx-autumn:sources")
        addDependency(project, GWT.ID, "$group:gdx-autumn-gwt")
        addDependency(project, GWT.ID, "$group:gdx-autumn-gwt:sources")
        addGwtInherit(project, "$group.autumn.gwt.GdxAutumnGwt")

        Kiwi().initiate(project)
    }
}

/**
 * Model-view-controller framework on top of Autumn DI and libGDX; no longer maintained.
 * AutumnMVC is actually used by gdx-liftoff and you can pull an updated fork of AutumnMVC code from the
 * "com.github.czyzby.autumn" Java package in gdx-liftoff's repo.
 */
@Extension
class AutumnMVC : LmlExtension() {
    override val id = "autumnMvc"
    override val url = "https://github.com/crashinvaders/gdx-lml/tree/master/mvc"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "$group:$name")

        addDependency(project, GWT.ID, "$group:$name:sources")
        addGwtInherit(project, "$group.autumn.mvc.GdxAutumnMvc")

        LML().initiate(project)
        Autumn().initiate(project)
    }
}

/**
 * Base class for MrStahlfelge's fork of gdx-lml websocket libraries.
 */
abstract class WebsocketExtension : ThirdPartyExtension() {
    override val defaultVersion = "1.9.10.3"
    override val group = "com.github.MrStahlfelge" // Matches JitPack root group.
    override val name = "gdx-websockets" // Matches JitPack root name.
    override val repository = Repository.JITPACK
    override val url = "https://github.com/MrStahlfelge/gdx-websockets"
}

/**
 * Cross-platform web sockets support; MrStahlfelge's fork.
 */
@Extension
class Websocket : WebsocketExtension() {
    override val id = "websocket"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "$group.gdx-websockets:core")

        addDependency(project, Shared.ID, "$group.gdx-websockets:core")

        addDesktopDependency(project, "$group.gdx-websockets:common")
        addDependency(project, Headless.ID, "$group.gdx-websockets:common")
        addDependency(project, iOS.ID, "$group.gdx-websockets:common")

        addDependency(project, Android.ID, "$group.gdx-websockets:common")
        addAndroidPermission(project, "android.permission.INTERNET")

        addDependency(project, GWT.ID, "$group.gdx-websockets:core:sources")
        addDependency(project, GWT.ID, "$group.gdx-websockets:html")
        addDependency(project, GWT.ID, "$group.gdx-websockets:html:sources")
        addGwtInherit(project, "$group.gdx-websockets.websocket.GdxWebSocket")
        addGwtInherit(project, "$group.gdx-websockets.websocket.GdxWebSocketGwt")
    }
}

/**
 * Cross-platform efficient serialization without reflection; MrStahlfelge's fork.
 */
@Extension
class WebsocketSerialization : WebsocketExtension() {
    override val id = "websocketSerialization"
    override val url = "https://github.com/MrStahlfelge/gdx-websockets/tree/master/serialization"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "$group.gdx-websockets:serialization")

        addDependency(project, Shared.ID, "$group.gdx-websockets:serialization")
        addDependency(project, Server.ID, "$group.gdx-websockets:serialization")

        addDependency(project, GWT.ID, "$group.gdx-websockets:serialization:sources")
        addGwtInherit(project, "$group.gdx-websockets.websocket.GdxWebSocketSerialization")

        Websocket().initiate(project)
    }
}
