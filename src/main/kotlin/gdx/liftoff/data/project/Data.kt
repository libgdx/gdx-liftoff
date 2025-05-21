package gdx.liftoff.data.project

import com.badlogic.gdx.files.FileHandle
import gdx.liftoff.config.Configuration
import gdx.liftoff.data.languages.Language
import gdx.liftoff.data.libraries.Library
import gdx.liftoff.data.libraries.Repository

/** Stores data represented by the main view over the settings section. */
data class BasicProjectData(
  val name: String,
  val rootPackage: String,
  val mainClass: String,
  val destination: FileHandle,
  val androidSdk: FileHandle,
)

/** Stores data represented by the advanced settings view. */
data class AdvancedProjectData(
  val version: String,
  val gdxVersion: String,
  val javaVersion: String,
  val gwtPluginVersion: String,
  val serverJavaVersion: String,
  val desktopJavaVersion: String,
  // Templates might force skin generation.
  var generateSkin: Boolean,
  val generateReadme: Boolean,
  val gradleTasks: ArrayList<String>,
  val generateEditorConfig: Boolean = true,
  val indentSize: Int = 4,
) {
  /**
   * Currently hard-coded to 35, since the Play Store requires that or will require that soon. This should be updated
   * as the Play Store requirement changes.
   */
  val androidSdkVersion: String = "35"

  /**
   * Should match the recommended/tested by the libGDX version.
   */
  val androidPluginVersion: String = "8.6.1"

  /**
   * Should be updated as Android's latest desugaring library updates, but must always be a version that works
   * with the currently-used Android Plugin Version (above).
   */
  val androidDesugaringLibraryVersion: String = "2.1.5"

  /**
   * Should match the recommended/tested by the libGDX version, but it's usually safe to upgrade to latest versions to keep up with iOS changes.
   */
  val robovmVersion: String = "2.3.23"

  /**
   * Will be set to 2.11.0 if using a Java version of at least 8; otherwise adapts to what the libGDX version uses.
   */
  val gwtVersion: String
    get() =
      if (Configuration.parseJavaVersion(javaVersion).compareTo(8.0) >= 0 ||
        (gdxVersion.startsWith("1.13."))
      ) {
        "2.11.0"
      } else if (gdxVersion.length == 5 && gdxVersion[4] != '9') {
        if (gdxVersion[4] < '5') "2.6.1" else "2.8.0"
      } else {
        "2.8.2"
      }

  /**
   * Version of xpenatan's TeaVM backend.
   */
  val gdxTeaVMVersion: String
    get() =
      Repository.MavenCentral.getLatestVersion(group = "com.github.xpenatan.gdx-teavm", name = "backend-teavm")
        ?: "1.2.1"

  /**
   * Version of the main TeaVM project.
   */
  val teaVMVersion: String
    get() =
      Repository.MavenCentral.getLatestVersion(group = "org.teavm", name = "teavm-core")
        ?: "0.12.0"

  /**
   * Version of the Gretty Gradle plugin used to serve compiled JavaScript applications.
   */
  val grettyVersion: String = "3.1.5"
}

/** Stores alternative JVM languages data. */
data class LanguagesData(
  val list: MutableList<Language>,
  val versions: Map<String, String>,
) {
  fun getVersion(id: String): String = versions[id] ?: ""

  inline fun <reified T : Language> selectLanguage() {
    if (list.any { it is T }) return
    list.add(T::class.java.getDeclaredConstructor().newInstance())
  }
}

/** Stores selected project dependencies. */
data class ExtensionsData(
  val officialExtensions: List<Library>,
  val thirdPartyExtensions: List<Library>,
) {
  private val ids = (officialExtensions + thirdPartyExtensions).map(Library::id).toSet()

  fun isSelected(id: String): Boolean = id in ids
}
