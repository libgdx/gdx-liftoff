@file:JvmName("Sample")

package gdx.liftoff

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Version
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.GdxNativesLoader
import com.github.czyzby.autumn.fcs.scanner.DesktopClassScanner
import gdx.liftoff.actions.GlobalActionContainer
import gdx.liftoff.config.Configuration
import gdx.liftoff.data.languages.Java
import gdx.liftoff.data.languages.Kotlin
import gdx.liftoff.data.languages.Language
import gdx.liftoff.data.libraries.Library
import gdx.liftoff.data.libraries.official.Box2D
import gdx.liftoff.data.libraries.official.Box2DLights
import gdx.liftoff.data.libraries.official.Freetype
import gdx.liftoff.data.libraries.official.OfficialExtension
import gdx.liftoff.data.libraries.unofficial.Formic
import gdx.liftoff.data.libraries.unofficial.KtxRepository
import gdx.liftoff.data.libraries.unofficial.RegExodus
import gdx.liftoff.data.libraries.unofficial.ShapeDrawer
import gdx.liftoff.data.libraries.unofficial.Stripe
import gdx.liftoff.data.libraries.unofficial.TenPatch
import gdx.liftoff.data.platforms.Android
import gdx.liftoff.data.platforms.Core
import gdx.liftoff.data.platforms.GWT
import gdx.liftoff.data.platforms.IOS
import gdx.liftoff.data.platforms.Lwjgl3
import gdx.liftoff.data.platforms.Platform
import gdx.liftoff.data.platforms.TeaVM
import gdx.liftoff.data.project.AdvancedProjectData
import gdx.liftoff.data.project.BasicProjectData
import gdx.liftoff.data.project.ExtensionsData
import gdx.liftoff.data.project.LanguagesData
import gdx.liftoff.data.project.Project
import gdx.liftoff.data.project.ProjectLogger
import gdx.liftoff.data.templates.Template
import gdx.liftoff.data.templates.official.ClassicTemplate
import gdx.liftoff.data.templates.official.KotlinClassicTemplate
import gdx.liftoff.data.templates.unofficial.KtxTemplate
import gdx.liftoff.views.Extension
import java.io.File
import java.util.Optional
import kotlin.system.exitProcess
import com.badlogic.gdx.utils.Array as GdxArray
import gdx.liftoff.views.Extension as GdxExtension

/** Determines which platforms, extensions, templates, etc. are used by the project generator. */
enum class Preset {
  /** Includes the official recommended platforms and a basic template. */
  DEFAULT {
    override val projectName: String
      get() = "gdx-liftoff-demo"
    override val rootPackage: String
      get() = "gdx.liftoff"
    override val platforms: List<Platform>
      get() = listOf(Core(), Lwjgl3(), Android(), IOS(), GWT())
    override val languages: List<Language> = emptyList()
    override val officialExtensions: Optional<List<Library>>
      get() = Optional.empty()
    override val thirdPartyExtensions: List<Library> = emptyList()
    override val template: Template
      get() = ClassicTemplate()
  },

  /** Includes the official platforms supporting Kotlin, as well as TeaVM. Uses a basic Kotlin template. */
  KOTLIN {
    override val projectName: String
      get() = "gdx-liftoff-demo-kotlin"
    override val rootPackage: String
      get() = "gdx.liftoff"
    override val platforms: List<Platform>
      get() = listOf(Core(), Lwjgl3(), Android(), IOS(), TeaVM())
    override val languages: List<Language>
      get() = listOf(Kotlin())
    override val officialExtensions: Optional<List<Library>>
      get() = Optional.empty()
    override val thirdPartyExtensions: List<Library> = emptyList()
    override val template: Template
      get() = KotlinClassicTemplate()
  },

  /** Includes the official platforms supporting Kotlin. Uses a KTX template. */
  KTX {
    override val projectName: String
      get() = "ktx-demo"
    override val rootPackage: String
      get() = "ktx.demo"
    override val platforms: List<Platform>
      get() = listOf(Core(), Lwjgl3(), Android(), IOS())
    override val languages: List<Language>
      get() = listOf(Kotlin())
    override val officialExtensions: Optional<List<Library>>
      get() = Optional.empty()
    override val thirdPartyExtensions: List<Library>
      get() {
        val scanner = DesktopClassScanner()
        return scanner.find<Extension>()
          .filter { !isOfficial(it) }
          .initiate<Library>()
          .filter { it.repository === KtxRepository }
      }
    override val template: Template
      get() = KtxTemplate()
    override val addSkin: Boolean = false
  },

  /** Includes the official platforms supporting Kotlin, as well as TeaVM. Omits unsupported KTX modules. */
  KTX_WEB {
    override val projectName: String
      get() = "ktx-demo-web"
    override val rootPackage: String
      get() = "ktx.demo"
    override val platforms: List<Platform>
      get() = listOf(Core(), Lwjgl3(), Android(), IOS(), TeaVM())
    override val languages: List<Language>
      get() = listOf(Kotlin())
    override val officialExtensions: Optional<List<Library>>
      get() = Optional.empty()
    override val thirdPartyExtensions: List<Library>
      get() {
        val scanner = DesktopClassScanner()
        return scanner.find<Extension>()
          .filter { !isOfficial(it) }
          .initiate<Library>()
          .filter { library ->
            library.repository === KtxRepository &&
              !library.id.endsWith("Async") &&
              listOf("artemis", "script").all { it !in library.id.lowercase() }
          }
      }
    override val template: Template
      get() = KtxTemplate()
    override val addSkin: Boolean = false
  },

  /** Includes Android and LWJGL3 projects, meant for users developing on Android devices.
   * Also includes Box2D, Box2D-Lights, GDX-Freetype, ShapeDrawer, TenPatch, and Stripe. */
  ANDROID_DEV {
    override val projectName: String
      get() = "gdx-android-dev-demo"
    override val rootPackage: String
      get() = "gdx.android"
    override val platforms: List<Platform>
      get() = listOf(Core(), Lwjgl3(), Android())
    override val languages: List<Language> = emptyList()
    override val officialExtensions: Optional<List<Library>>
      get() = Optional.of(listOf(Box2D(), Box2DLights(), Freetype()))
    override val thirdPartyExtensions: List<Library> = listOf(ShapeDrawer(), TenPatch(), Stripe())
    override val template: Template
      get() = ClassicTemplate()
  },

  /** Includes GWT and LWJGL3 projects, meant for users developing an HTML game that can also be played offline.
   * Also includes Box2D, ShapeDrawer, TenPatch, Stripe, Formic, and RegExodus. */
  GWT_DEV {
    override val projectName: String
      get() = "gdx-gwt-dev-demo"
    override val rootPackage: String
      get() = "gdx.gwt"
    override val platforms: List<Platform>
      get() = listOf(Core(), Lwjgl3(), GWT())
    override val languages: List<Language> = emptyList()
    override val officialExtensions: Optional<List<Library>>
      get() = Optional.of(listOf(Box2D()))
    override val thirdPartyExtensions: List<Library> = listOf(ShapeDrawer(), TenPatch(), Stripe(), Formic(), RegExodus())
    override val template: Template
      get() = ClassicTemplate()
  },

  /** Includes TeaVM and LWJGL3 projects with the Kotlin language, meant for users developing an HTML game that can also
   * be played offline, in Kotlin. You may prefer KTX_WEB if you want to use KTX.
   * Also includes Box2D, ShapeDrawer, TenPatch and Stripe. */
  TEA_DEV {
    override val projectName: String
      get() = "gdx-gwt-dev-demo"
    override val rootPackage: String
      get() = "gdx.gwt"
    override val platforms: List<Platform>
      get() = listOf(Core(), Lwjgl3(), TeaVM())
    override val languages: List<Language> = listOf(Kotlin())
    override val officialExtensions: Optional<List<Library>>
      get() = Optional.of(listOf(Box2D()))
    override val thirdPartyExtensions: List<Library> = listOf(ShapeDrawer(), TenPatch(), Stripe())
    override val template: Template
      get() = KotlinClassicTemplate()
  };

  abstract val projectName: String
  abstract val rootPackage: String
  abstract val platforms: List<Platform>
  abstract val languages: List<Language>
  abstract val officialExtensions: Optional<List<Library>>
  abstract val thirdPartyExtensions: List<Library>
  abstract val template: Template
  open val addSkin: Boolean = true

  val languagesData: LanguagesData
    get() = LanguagesData(languages.toMutableList(), languages.associate { it.id to it.version })
}

fun getPreset(arguments: Array<String>): Preset = when {
  arguments.isEmpty() -> Preset.DEFAULT
  else -> {
    val name = arguments.first()
    try {
      Preset.valueOf(name.uppercase())
    } catch (exception: IllegalArgumentException) {
      Preset.DEFAULT
    }
  }
}

fun main(arguments: Array<String>) {
  GdxNativesLoader.load()
  Gdx.files = Lwjgl3Files()

  val preset = getPreset(arguments)
  val scanner = DesktopClassScanner()
  val officialExtensions = scanner.find<GdxExtension>().filter(::isOfficial).initiate<OfficialExtension>()
  val basicData = BasicProjectData(
    name = preset.projectName,
    rootPackage = preset.rootPackage,
    mainClass = "Main",
    destination = FileHandle(File("build/dist/sample")),
    androidSdk = FileHandle(File("."))
  )
  val defaultJavaVersion = Java().version
  val defaults = GlobalActionContainer()
  val advancedData = AdvancedProjectData(
    version = Configuration.VERSION,
    gdxVersion = Version.VERSION,
    javaVersion = defaultJavaVersion,
    androidPluginVersion = defaults.getDefaultAndroidPluginVersion(),
    robovmVersion = defaults.getDefaultRoboVMVersion(),
    gwtPluginVersion = defaults.getDefaultGwtPluginVersion(),
    serverJavaVersion = defaultJavaVersion,
    desktopJavaVersion = defaultJavaVersion,
    generateSkin = preset.addSkin,
    generateReadme = true,
    gradleTasks = mutableListOf()
  )
  val extensions = ExtensionsData(
    officialExtensions = preset.officialExtensions.orElse(officialExtensions),
    thirdPartyExtensions = preset.thirdPartyExtensions
  )

  val project = Project(
    basic = basicData,
    advanced = advancedData,
    platforms = preset.platforms.associateBy { it.id },
    languages = preset.languagesData,
    extensions = extensions,
    template = preset.template
  )
  project.generate()
  project.includeGradleWrapper(NullLogger, executeGradleTasks = false)
  exitProcess(0)
}

/** No-op logger for interfacing with the project generator. */
object NullLogger : ProjectLogger {
  override fun log(message: String) {}
  override fun logNls(bundleLine: String) {}
}

inline fun <reified T : Annotation> DesktopClassScanner.find(): GdxArray<Class<*>> =
  findClassesAnnotatedWith(Root::class.java, listOf(T::class.java))

fun isOfficial(extensionClass: Class<*>): Boolean =
  extensionClass.getAnnotation(GdxExtension::class.java).official

inline fun <reified T : Any> Iterable<Class<*>>.initiate(): List<T> =
  map { it.getDeclaredConstructor().newInstance() as T }
