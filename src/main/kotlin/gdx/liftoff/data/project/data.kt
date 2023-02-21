package gdx.liftoff.data.project

import com.badlogic.gdx.files.FileHandle
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
  val androidSdkVersion: String,
  val androidPluginVersion: String,
  val robovmVersion: String,
  val gwtPluginVersion: String,
  val serverJavaVersion: String,
  val desktopJavaVersion: String,
  // Templates might force skin generation.
  var generateSkin: Boolean,
  val generateReadme: Boolean,
  val gradleTasks: MutableList<String>,
  val generateEditorConfig: Boolean = true,
  val indentSize: Int = 4,
) {
  /**
   * Can be set manually to 2.9.0 or 2.10.0 if using an alternative backend; see generated html/build.gradle .
   */
  val gwtVersion: String
    get() = if (gdxVersion.length == 5 && gdxVersion[4] != '9') {
      if (gdxVersion[4] < '5') "2.6.1" else "2.8.0"
    } else "2.8.2"

  /**
   * Version of xpenatan's TeaVM backend.
   */
  val gdxTeaVMVersion: String
    get() = Repository.MavenCentral.getLatestVersion(group = "com.github.xpenatan.gdx-teavm", name = "backend-teavm")
      ?: "1.0.0-b3"

  /**
   * Version of the Gretty Gradle plugin used to serve compiled JavaScript applications.
   */
  val grettyVersion: String = "3.1.0"
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
