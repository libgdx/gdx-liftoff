package gdx.liftoff.data.languages

import gdx.liftoff.data.files.SourceDirectory
import gdx.liftoff.data.files.path
import gdx.liftoff.data.project.Project

/**
 * Adds Java support to the project.
 */
class Java : Language {
  override val id = "java-library"
  override val version = "11"

  override fun initiate(project: Project) {
    project.rootGradle.plugins.add(id)
    project.platforms.values.forEach { project.files.add(SourceDirectory(it.id, path("src", "main", "java"))) }
  }
}
