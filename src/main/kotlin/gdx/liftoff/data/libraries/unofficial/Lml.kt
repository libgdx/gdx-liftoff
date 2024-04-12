@file:Suppress("unused") // Extension classes accessed via reflection.

package gdx.liftoff.data.libraries.unofficial

import gdx.liftoff.data.libraries.Library
import gdx.liftoff.data.libraries.Repository
import gdx.liftoff.data.libraries.SingleVersionRepository
import gdx.liftoff.data.libraries.camelCaseToKebabCase
import gdx.liftoff.data.platforms.Android
import gdx.liftoff.data.platforms.Core
import gdx.liftoff.data.platforms.GWT
import gdx.liftoff.data.platforms.Headless
import gdx.liftoff.data.platforms.IOS
import gdx.liftoff.data.platforms.Server
import gdx.liftoff.data.platforms.Shared
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.Extension

private const val defaultGroup = "com.crashinvaders.lml"
private const val fallbackVersion = "1.10.1.12.0"

/**
 * Various utilities for libGDX APIs including GUI building and dependency injection.
 * @author czyzby
 * @author metaphore (Crashinvaders)
 */
abstract class LmlExtension : Library {
  /**
   * Latest version of gdx-lml libraries from the Crashinvaders fork.
   */
  override val defaultVersion = fallbackVersion
  override val repository = LmlRepository
  override val group = defaultGroup
  override val official = false
  override val name: String
    get() = "gdx-" + id.camelCaseToKebabCase()
  override val url: String
    get() = "https://github.com/crashinvaders/gdx-lml/tree/master/" + id.camelCaseToKebabCase()

  override fun initiate(project: Project) {
    project.properties["lmlVersion"] = LmlRepository.version
    addDependency(project, Core.ID, "$group:$name")
    addDependency(project, GWT.ID, "$group:$name:sources")
    initiateDependencies(project)
  }

  open fun initiateDependencies(project: Project) {}

  override fun addDependency(project: Project, platform: String, dependency: String) {
    if (dependency.count { it == ':' } > 1) {
      super.addDependency(project, platform, dependency.substringBeforeLast(':') + ":\$lmlVersion:" + dependency.substringAfterLast(':'))
    } else {
      super.addDependency(project, platform, "$dependency:\$lmlVersion")
    }
  }

  fun addExternalDependency(project: Project, platform: String, dependency: String) {
    super.addDependency(project, platform, dependency)
  }
}

/**
 * Fetches and caches latest gdx-lml libraries version.
 */
object LmlRepository : SingleVersionRepository(fallbackVersion) {
  override fun fetchLatestVersion(): String? {
    return Repository.MavenCentral.getLatestVersion(defaultGroup, "gdx-lml")
  }
}

/**
 * Guava-inspired general libGDX utilities; now maintained in the crashinvaders fork.
 */
@Extension
class Kiwi : LmlExtension() {
  override val id = "kiwi"

  override fun initiateDependencies(project: Project) {
    addGwtInherit(project, "com.github.czyzby.kiwi.GdxKiwi")
  }
}

/**
 * Parser of HTML-like templates that produces Scene2D widgets; now maintained in the crashinvaders fork.
 */
@Extension
class LML : LmlExtension() {
  override val id = "lml"

  override fun initiateDependencies(project: Project) {
    addGwtInherit(project, "com.github.czyzby.lml.GdxLml")

    Kiwi().initiate(project)
  }
}

/**
 * Parser of HTML-like templates that produces VisUI widgets; now maintained in the crashinvaders fork.
 */
@Extension
class LMLVis : LmlExtension() {
  override val id = "lmlVis"

  override fun initiateDependencies(project: Project) {
    addGwtInherit(project, "com.github.czyzby.lml.vis.GdxLmlVis")

    LML().initiate(project)
    VisUI().initiate(project)
  }
}

/**
 * Dependency injection mechanism with component scan using libGDX reflection API; now maintained in
 * the crashinvaders fork.
 */
@Extension
class Autumn : LmlExtension() {
  override val id = "autumn"

  override fun initiateDependencies(project: Project) {
    addDesktopDependency(project, "$group:gdx-autumn-fcs")
    addDependency(project, Headless.ID, "$group:gdx-autumn-fcs")

    addDependency(project, Android.ID, "$group:gdx-autumn-android")

    addDependency(project, GWT.ID, "$group:gdx-autumn-gwt")
    addDependency(project, GWT.ID, "$group:gdx-autumn-gwt:sources")
    addGwtInherit(project, "com.github.czyzby.autumn.gwt.GdxAutumnGwt")

    Kiwi().initiate(project)
  }
}

/**
 * Model-view-controller framework on top of Autumn DI and libGDX; now maintained in the crashinvaders fork.
 */
@Extension
class AutumnMVC : LmlExtension() {
  override val id = "autumnMvc"
  override val url = "https://github.com/crashinvaders/gdx-lml/tree/master/mvc"

  override fun initiateDependencies(project: Project) {
    addGwtInherit(project, "com.github.czyzby.autumn.mvc.GdxAutumnMvc")

    LML().initiate(project)
    Autumn().initiate(project)
  }
}

/**
 * Base class for MrStahlfelge's fork of gdx-lml websocket libraries.
 */
abstract class WebSocketExtension : ThirdPartyExtension() {
  override val defaultVersion = "1.9.10.3"
  override val group = "com.github.MrStahlfelge" // Matches JitPack root group.
  override val name = "gdx-websockets" // Matches JitPack root name.
  override val repository = Repository.JitPack
  override val url = "https://github.com/MrStahlfelge/gdx-websockets"
}

/**
 * Cross-platform web sockets support; MrStahlfelge's fork.
 */
@Extension
class WebSocket : WebSocketExtension() {
  override val id = "websocket"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "$group.gdx-websockets:core")

    addDependency(project, Shared.ID, "$group.gdx-websockets:core")

    addDesktopDependency(project, "$group.gdx-websockets:common")
    addDependency(project, Headless.ID, "$group.gdx-websockets:common")
    addDependency(project, IOS.ID, "$group.gdx-websockets:common")

    addDependency(project, Android.ID, "$group.gdx-websockets:common")
    addAndroidPermission(project, "android.permission.INTERNET")

    addDependency(project, GWT.ID, "$group.gdx-websockets:core:sources")
    addDependency(project, GWT.ID, "$group.gdx-websockets:html:sources")

    addGwtInherit(project, "com.github.czyzby.websocket.GdxWebSocket")
    addGwtInherit(project, "com.github.czyzby.websocket.GdxWebSocketGwt")
  }
}

/**
 * Cross-platform efficient serialization without reflection; MrStahlfelge's fork.
 */
@Extension
class WebSocketSerialization : WebSocketExtension() {
  override val id = "websocketSerialization"
  override val url = "https://github.com/MrStahlfelge/gdx-websockets/tree/master/serialization"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "$group.gdx-websockets:serialization")

    addDependency(project, Shared.ID, "$group.gdx-websockets:serialization")
    addDependency(project, Server.ID, "$group.gdx-websockets:serialization")

    addDependency(project, GWT.ID, "$group.gdx-websockets:serialization:sources")
    addGwtInherit(project, "com.github.czyzby.websocket.GdxWebSocketSerialization")

    WebSocket().initiate(project)
  }
}
