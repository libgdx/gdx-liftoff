@file:Suppress("unused") // Extension classes accessed via reflection.

package gdx.liftoff.data.libraries.unofficial

import com.badlogic.gdx.Gdx
import com.github.kittinunf.fuel.Fuel.get
import gdx.liftoff.data.libraries.Library
import gdx.liftoff.data.libraries.Repository
import gdx.liftoff.data.libraries.SingleVersionRepository
import gdx.liftoff.data.libraries.camelCaseToKebabCase
import gdx.liftoff.data.libraries.official.AI
import gdx.liftoff.data.libraries.official.Ashley
import gdx.liftoff.data.libraries.official.Box2D
import gdx.liftoff.data.libraries.official.Freetype
import gdx.liftoff.data.platforms.Core
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.Extension

private const val defaultGroup = "io.github.libktx"
private const val fallbackVersion = "1.12.1-rc1"

/**
 * Modular Kotlin utilities.
 * @author czyzby
 * @author libKTX group
 */
abstract class KtxExtension : Library {
  override val defaultVersion = fallbackVersion
  override val official = false
  override val repository = KtxRepository
  override val group = defaultGroup
  override val name
    get() = id.camelCaseToKebabCase()
  override val url: String
    get() = "https://github.com/libktx/ktx/tree/master/" + id.removePrefix("ktx").camelCaseToKebabCase()

  override fun initiate(project: Project) {
    project.properties["ktxVersion"] = KtxRepository.version
    addDependency(project, Core.ID, "$group:$name")
    initiateDependencies(project)
  }

  open fun initiateDependencies(project: Project) {}

  override fun addDependency(project: Project, platform: String, dependency: String) {
    super.addDependency(project, platform, "$dependency:\$ktxVersion")
  }

  fun addExternalDependency(project: Project, platform: String, dependency: String) {
    super.addDependency(project, platform, dependency)
  }
}

/**
 * Fetches and caches the latest KTX version.
 */
object KtxRepository : SingleVersionRepository(fallbackVersion) {
  override fun fetchLatestVersion(): String? {
    return try {
      // Fetching and caching KTX version from the repo:
      get("https://raw.githubusercontent.com/libktx/ktx/master/version.txt").timeout(30000).responseString().third.get().trim()
    } catch (exception: Exception) {
      Gdx.app.error("gdx-liftoff", "Unable to fetch KTX version from the repository.", exception)
      Repository.MavenCentral.getLatestVersion(defaultGroup, "ktx-app")
    }
  }
}

/**
 * Kotlin utilities for Scene2D actors API.
 */
@Extension
class KtxActors : KtxExtension() {
  override val id = "ktxActors"
}

/**
 * General application listener utilities for Kotlin applications.
 */
@Extension
class KtxApp : KtxExtension() {
  override val id = "ktxApp"
}

/**
 * Kotlin utilities for gdx-ai usage.
 */
@Extension
class KtxAi : KtxExtension() {
  override val id = "ktxAi"

  override fun initiateDependencies(project: Project) {
    AI().initiate(project)
  }
}

/**
 * Utilities for Artemis-odb entity component system.
 * @author deviodesign
 */
@Extension
class KtxArtemis : KtxExtension() {
  override val id = "ktxArtemis"

  override fun initiateDependencies(project: Project) {
    ArtemisOdb().initiate(project)
  }
}

/**
 * Utilities for Ashley entity component system.
 * @author Jkly
 */
@Extension
class KtxAshley : KtxExtension() {
  override val id = "ktxAshley"

  override fun initiateDependencies(project: Project) {
    Ashley().initiate(project)
  }
}

/**
 * Kotlin utilities for assets management.
 */
@Extension
class KtxAssets : KtxExtension() {
  override val id = "ktxAssets"
}

/**
 * Kotlin utilities for asynchronous assets management.
 */
@Extension
class KtxAssetsAsync : KtxExtension() {
  override val id = "ktxAssetsAsync"

  override fun initiateDependencies(project: Project) {
    KtxAssets().initiate(project)
    KtxAsync().initiate(project)
  }
}

/**
 * Kotlin coroutines context and utilities for asynchronous operations.
 */
@Extension
class KtxAsync : KtxExtension() {
  override val id = "ktxAsync"

  override fun initiateDependencies(project: Project) {
    KotlinxCoroutines().initiate(project)
  }
}

/**
 * Kotlin Box2D utilities and type-safe builders.
 */
@Extension
class KtxBox2D : KtxExtension() {
  override val id = "ktxBox2d"

  override fun initiateDependencies(project: Project) {
    Box2D().initiate(project)
  }
}

/**
 * Kotlin utilities for libGDX collections.
 */
@Extension
class KtxCollections : KtxExtension() {
  override val id = "ktxCollections"
}

/**
 * Kotlin utilities for loading TrueType fonts via Freetype.
 */
@Extension
class KtxFreetype : KtxExtension() {
  override val id = "ktxFreetype"

  override fun initiateDependencies(project: Project) {
    Freetype().initiate(project)
  }
}

/**
 * Kotlin utilities for asynchronous loading of TrueType fonts via Freetype.
 */
@Extension
class KtxFreetypeAsync : KtxExtension() {
  override val id = "ktxFreetypeAsync"

  override fun initiateDependencies(project: Project) {
    KtxFreetype().initiate(project)
    KtxAsync().initiate(project)
  }
}

/**
 * Kotlin utilities for handling graphics in libGDX applications.
 */
@Extension
class KtxGraphics : KtxExtension() {
  override val id = "ktxGraphics"
}

/**
 * Kotlin utilities for internationalization.
 */
@Extension
class KtxI18n : KtxExtension() {
  override val id = "ktxI18n"
}

/**
 * Kotlin dependency injection without reflection usage.
 */
@Extension
class KtxInject : KtxExtension() {
  override val id = "ktxInject"
}

/**
 * libGDX JSON serialization utilities for Kotlin applications.
 * @author maltaisn
 */
@Extension
class KtxJson : KtxExtension() {
  override val id = "ktxJson"
}

/**
 * Kotlin utilities for zero-overhead logging.
 */
@Extension
class KtxLog : KtxExtension() {
  override val id = "ktxLog"
}

/**
 * Kotlin utilities for math-related classes.
 */
@Extension
class KtxMath : KtxExtension() {
  override val id = "ktxMath"
}

/**
 * libGDX preferences utilities for applications developed with Kotlin.
 * @author Quillraven
 */
@Extension
class KtxPreferences : KtxExtension() {
  override val id = "ktxPreferences"
}

/**
 * libGDX reflection utilities for applications developed with Kotlin.
 */
@Extension
class KtxReflect : KtxExtension() {
  override val id = "ktxReflect"
}

/**
 * Kotlin type-safe builders for Scene2D GUI.
 */
@Extension
class KtxScene2D : KtxExtension() {
  override val id = "ktxScene2d"
}

/**
 * Kotlin type-safe builders for Scene2D widget styles.
 */
@Extension
class KtxStyle : KtxExtension() {
  override val id = "ktxStyle"
}

/**
 * Tiled utilities for libGDX applications written with Kotlin.
 * @author Quillraven
 */
@Extension
class KtxTiled : KtxExtension() {
  override val id = "ktxTiled"
}

/**
 * Kotlin type-safe builders for VisUI widgets.
 */
@Extension
class KtxVis : KtxExtension() {
  override val id = "ktxVis"

  override fun initiateDependencies(project: Project) {
    VisUI().initiate(project)
  }
}

/**
 * Kotlin type-safe builders for VisUI widget styles.
 */
@Extension
class KtxVisStyle : KtxExtension() {
  override val id = "ktxVisStyle"

  override fun initiateDependencies(project: Project) {
    KtxStyle().initiate(project)
    VisUI().initiate(project)
  }
}
