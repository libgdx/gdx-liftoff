package com.github.czyzby.setup.data.libs.unofficial

import com.github.czyzby.setup.data.platforms.*
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.Extension

/**
 * Version of Czyzby's libraries; this is a snapshot release because Czyzby is unlikely to make a stable release.
 * @author MJ
 */
const val AUTUMN_VERSION = "1.9.1.9.11-SNAPSHOT"

/**
 * Guava-inspired libGDX utilities; no longer maintained.
 * Kiwi is actually used by gdx-liftoff and you can pull an updated fork of Kiwi code from the "com.github.czyzby.kiwi"
 * Java package in gdx-liftoff's repo.
 * @author MJ
 */
@Extension
class Kiwi : ThirdPartyExtension() {
    override val id = "kiwi"
    override val defaultVersion = AUTUMN_VERSION
    override val url = "https://github.com/czyzby/gdx-lml/tree/master/kiwi"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.czyzby:gdx-kiwi")

        addDependency(project, GWT.ID, "com.github.czyzby:gdx-kiwi:sources")
        addGwtInherit(project, "com.github.czyzby.kiwi.GdxKiwi")
    }
}

/**
 * Parser of HTML-like templates that produces Scene2D widgets; no longer maintained.
 * LML is actually used by gdx-liftoff and you can pull an updated fork of LML code from the "com.github.czyzby.lml"
 * Java package in gdx-liftoff's repo.
 * @author MJ
 */
@Extension
class LML : ThirdPartyExtension() {
    override val id = "lml"
    override val defaultVersion = AUTUMN_VERSION
    override val url = "https://github.com/czyzby/gdx-lml/tree/master/lml"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.czyzby:gdx-lml")

        addDependency(project, GWT.ID, "com.github.czyzby:gdx-lml:sources")
        addGwtInherit(project, "com.github.czyzby.lml.GdxLml")

        Kiwi().initiate(project)
    }
}

/**
 * Parser of HTML-like templates that produces VisUI widgets; no longer maintained.
 * LMLVis is actually used by gdx-liftoff and you can pull an updated fork of LMLVis code from the
 * "com.github.czyzby.lml" Java package in gdx-liftoff's repo.
 * @author MJ
 * @author Kotcrab
 */
@Extension
class LMLVis : ThirdPartyExtension() {
    override val id = "lmlVis"
    override val defaultVersion = AUTUMN_VERSION
    override val url = "https://github.com/czyzby/gdx-lml/tree/master/lml-vis"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.czyzby:gdx-lml-vis")

        addDependency(project, GWT.ID, "com.github.czyzby:gdx-lml-vis:sources")
        addGwtInherit(project, "com.github.czyzby.lml.vis.GdxLmlVis")

        LML().initiate(project)
        VisUI().initiate(project)
    }
}

/**
 * Dependency injection mechanism with component scan using libGDX reflection API; no longer maintained.
 * Autumn is actually used by gdx-liftoff and you can pull an updated fork of Autumn code from the
 * "com.github.czyzby.autumn" Java package in gdx-liftoff's repo.
 * @author MJ
 */
@Extension
class Autumn : ThirdPartyExtension() {
    override val id = "autumn"
    override val defaultVersion = AUTUMN_VERSION
    override val url = "https://github.com/czyzby/gdx-lml/tree/master/autumn"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.czyzby:gdx-autumn")

        addDesktopDependency(project, "com.github.czyzby:gdx-autumn-fcs")

        addDependency(project, Headless.ID, "com.github.czyzby:gdx-autumn-fcs")

        addDependency(project, Android.ID, "com.github.czyzby:gdx-autumn-android")

        addDependency(project, GWT.ID, "com.github.czyzby:gdx-autumn:sources")
        addDependency(project, GWT.ID, "com.github.czyzby:gdx-autumn-gwt")
        addDependency(project, GWT.ID, "com.github.czyzby:gdx-autumn-gwt:sources")
        addGwtInherit(project, "com.github.czyzby.autumn.gwt.GdxAutumnGwt")

        Kiwi().initiate(project)
    }
}

/**
 * Model-view-controller framework on top of Autumn DI and libGDX; no longer maintained.
 * AutumnMVC is actually used by gdx-liftoff and you can pull an updated fork of AutumnMVC code from the
 * "com.github.czyzby.autumn" Java package in gdx-liftoff's repo.
 * @author MJ
 */
@Extension
class AutumnMVC : ThirdPartyExtension() {
    override val id = "autumnMvc"
    override val defaultVersion = AUTUMN_VERSION
    override val url = "https://github.com/czyzby/gdx-lml/tree/master/mvc"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.czyzby:gdx-autumn-mvc")

        addDependency(project, GWT.ID, "com.github.czyzby:gdx-autumn-mvc:sources")
        addGwtInherit(project, "com.github.czyzby.autumn.mvc.GdxAutumnMvc")

        LML().initiate(project)
        Autumn().initiate(project)
    }
}

/**
 * Cross-platform web sockets support; MrStahlfelge's fork.
 * @author MJ
 * @author MrStahlfelge
 */
@Extension
class Websocket : ThirdPartyExtension() {
    override val id = "websocket"
    override val defaultVersion = "1.9.10.3"
    override val url = "https://github.com/MrStahlfelge/gdx-websockets"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.MrStahlfelge.gdx-websockets:core")

        addDependency(project, Shared.ID, "com.github.MrStahlfelge.gdx-websockets:core")

        addDesktopDependency(project, "com.github.MrStahlfelge.gdx-websockets:common")
        addDependency(project, Headless.ID, "com.github.MrStahlfelge.gdx-websockets:common")
        addDependency(project, iOS.ID, "com.github.MrStahlfelge.gdx-websockets:common")

        addDependency(project, Android.ID, "com.github.MrStahlfelge.gdx-websockets:common")
        addAndroidPermission(project, "android.permission.INTERNET")

        addDependency(project, GWT.ID, "com.github.MrStahlfelge.gdx-websockets:core:sources")
        addDependency(project, GWT.ID, "com.github.MrStahlfelge.gdx-websockets:html")
        addDependency(project, GWT.ID, "com.github.MrStahlfelge.gdx-websockets:html:sources")
        addGwtInherit(project, "com.github.czyzby.websocket.GdxWebSocket")
        addGwtInherit(project, "com.github.czyzby.websocket.GdxWebSocketGwt")
    }
}

/**
 * Cross-platform efficient serialization without reflection; MrStahlfelge's fork.
 * @author MJ
 * @author MrStahlfelge
 */
@Extension
class WebsocketSerialization : ThirdPartyExtension() {
    override val id = "websocketSerialization"
    override val defaultVersion = "1.9.10.3"
    override val url = "https://github.com/MrStahlfelge/gdx-websockets/tree/master/serialization"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.MrStahlfelge.gdx-websockets:serialization")

        addDependency(project, Shared.ID, "com.github.MrStahlfelge.gdx-websockets:serialization")
        addDependency(project, Server.ID, "com.github.MrStahlfelge.gdx-websockets:serialization")

        addDependency(project, GWT.ID, "com.github.MrStahlfelge.gdx-websockets:serialization:sources")
        addGwtInherit(project, "com.github.czyzby.websocket.GdxWebSocketSerialization")

        Websocket().initiate(project)
    }
}
