package gdx.liftoff.data.libraries

import gdx.liftoff.data.platforms.Android
import gdx.liftoff.data.platforms.AndroidGradleFile
import gdx.liftoff.data.platforms.GWT
import gdx.liftoff.data.platforms.Lwjgl2
import gdx.liftoff.data.platforms.Lwjgl3
import gdx.liftoff.data.project.Project

/**
 * Interface shared by all libGDX extensions.
 */
interface Library {
  /** Unique ID of the library used throughout the project. */
  val id: String

  /** Project URL. */
  val url: String

  /** True if this library is maintained by libGDX organization. False otherwise.*/
  val official: Boolean

  /** Maven repository that contains the artifacts. */
  val repository: Repository

  /** Group of the main dependency used to determine the version. */
  val group: String

  /** Name of the main dependency used to determine the version. */
  val name: String

  /** Version of the library to use. */
  val defaultVersion: String

  /**
   * Gets the [defaultVersion]. Doesn't try to fetch new versions of libraries!
   * Fetching multiple libraries can take a while with the default timeout of 30 seconds per library.
   * Updating Liftoff should ensure that all libraries available via checkboxes use compatible versions.
   * Using an older Liftoff should still download working versions even if newer (potentially incompatible)
   * dependencies are available, and manually updating dependencies is as simple as editing gradle.properties .
   */
  val version: String
    get() {
      return defaultVersion
    }

//  /* Latest version of the library fetched from the Maven repository or [defaultVersion].
//   * If the "latest" version has been successfully obtained, it is still compared with [NaturalTextComparator]
//   * to try to retrieve the newer version. */
//  val version: String
//    get() {
//      val retrieved = repository.getLatestVersion(group, name)
//      return if (retrieved == null) {
//        defaultVersion
//      } else if (NaturalTextComparator.CASE_INSENSITIVE.compare(retrieved, defaultVersion) >= 0) {
//        retrieved
//      } else {
//        defaultVersion
//      }
//    }

  /**
   * @param project is currently generated and should have this library included.
   */
  fun initiate(project: Project)

  fun addDependency(
    project: Project,
    platform: String,
    dependency: String,
  ) {
    if (project.hasPlatform(platform)) {
      project.getGradleFile(platform).addDependency(dependency)
    }
  }

  fun addSpecialDependency(
    project: Project,
    platform: String,
    dependency: String,
  ) {
    if (project.hasPlatform(platform)) {
      project.getGradleFile(platform).addSpecialDependency(dependency)
    }
  }

  fun addDesktopDependency(
    project: Project,
    dependency: String,
  ) {
    addDependency(project, Lwjgl2.ID, dependency)
    addDependency(project, Lwjgl3.ID, dependency)
  }

  fun addNativeAndroidDependency(
    project: Project,
    dependency: String,
  ) {
    if (project.hasPlatform(Android.ID)) {
      val gradle = project.getGradleFile(Android.ID) as AndroidGradleFile
      gradle.addNativeDependency(dependency)
    }
  }

  fun addGwtInherit(
    project: Project,
    inherit: String,
  ) {
    if (project.hasPlatform(GWT.ID)) {
      project.gwtInherits.add(inherit)
    }
  }

  fun addAndroidPermission(
    project: Project,
    permissionName: String,
  ) {
    if (project.hasPlatform(Android.ID)) {
      project.androidPermissions.add(permissionName)
    }
  }
}
