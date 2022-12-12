package gdx.liftoff.data.platforms

import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.project.Project

/**
 * Mock-up platform. Represents the `assets/` folder.
 */
class Assets : Platform {
  companion object {
    const val ID = "assets"
  }

  override val id = ID
  override val order = -1
  override val description = ""
  override val isStandard = false

  override fun createGradleFile(project: Project): GradleFile =
    throw UnsupportedOperationException("This is a mock-up project with no Gradle file.")

  override fun initiate(project: Project) =
    throw UnsupportedOperationException("This is a mock-up project which should not be initiated.")
}
