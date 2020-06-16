package com.github.czyzby.setup.data.libs.unofficial

import com.github.czyzby.setup.data.platforms.*
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.Extension

/**
 * Version of Czyzby's libraries.
 * @author MJ
 */
const val AUTUMN_VERSION = "1.9.1.9.6"

/**
 * Guava-inspired LibGDX utilities.
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
 * Parser of HTML-like templates that produces Scene2D widgets.
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
 * Parser of HTML-like templates that produces VisUI widgets.
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
 * Dependency injection mechanism with component scan using LibGDX reflection API.
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
 * Model-view-controller framework on top of Autumn DI and LibGDX.
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
 * Cross-platform web sockets support.
 * @author MJ
 */
@Extension
class Websocket : ThirdPartyExtension() {
    override val id = "websocket"
    override val defaultVersion = AUTUMN_VERSION
    override val url = "https://github.com/czyzby/gdx-lml/tree/master/websocket"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.czyzby:gdx-websocket")

        addDependency(project, Shared.ID, "com.github.czyzby:gdx-websocket")

        addDesktopDependency(project, "com.github.czyzby:gdx-websocket-common")
        addDependency(project, Headless.ID, "com.github.czyzby:gdx-websocket-common")
        addDependency(project, iOS.ID, "com.github.czyzby:gdx-websocket-common")

        addDependency(project, Android.ID, "com.github.czyzby:gdx-websocket-common")
        addAndroidPermission(project, "android.permission.INTERNET")

        addDependency(project, GWT.ID, "com.github.czyzby:gdx-websocket:sources")
        addDependency(project, GWT.ID, "com.github.czyzby:gdx-websocket-gwt")
        addDependency(project, GWT.ID, "com.github.czyzby:gdx-websocket-gwt:sources")
        addGwtInherit(project, "com.github.czyzby.websocket.GdxWebSocketGwt")
    }
}

/**
 * Cross-platform efficient serialization without reflection.
 * @author MJ
 */
@Extension
class WebsocketSerialization : ThirdPartyExtension() {
    override val id = "websocketSerialization"
    override val defaultVersion = AUTUMN_VERSION
    override val url = "https://github.com/czyzby/gdx-lml/tree/master/websocket/natives/serialization"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.czyzby:gdx-websocket-serialization")

        addDependency(project, Shared.ID, "com.github.czyzby:gdx-websocket-serialization")
        addDependency(project, Server.ID, "com.github.czyzby:gdx-websocket-serialization")

        addDependency(project, GWT.ID, "com.github.czyzby:gdx-websocket-serialization:sources")
        addGwtInherit(project, "com.github.czyzby.websocket.GdxWebSocketSerialization")

        Websocket().initiate(project)
    }
}
