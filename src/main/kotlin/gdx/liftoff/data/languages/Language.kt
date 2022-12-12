package gdx.liftoff.data.languages

import gdx.liftoff.data.platforms.Core
import gdx.liftoff.data.platforms.Shared
import gdx.liftoff.data.project.Project

/**
 * Common interface for additional JVM languages. If the language is optional, its class should be annotated with
 * JvmLanguage.
 */
interface Language {
  val id: String
  val version: String

  /**
   * Adds language-specific dependencies and plugins.
   * @param project is being generated.
   */
  fun initiate(project: Project)

  fun addDependency(project: Project, dependency: String) {
    project.getGradleFile(Core.ID).addDependency(dependency)
    if (project.hasPlatform(Shared.ID)) {
      project.getGradleFile(Shared.ID).addDependency(dependency)
    }
  }
}
