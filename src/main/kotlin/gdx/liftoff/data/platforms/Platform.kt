package gdx.liftoff.data.platforms

import com.badlogic.gdx.graphics.Pixmap
import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.GeneratedImageFile
import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.project.Project

/**
 * Common interface for all supported platforms. Implementation should be annotated with GdxPlatform.
 */
interface Platform {
  /**
   * Unique ID of the platform.
   */
  val id: String

  /**
   * Description of the platform as it appears in the generated project README files.
   */
  val description: String

  /**
   * Display order of the platform within the application.
   */
  val order: Int

  /**
   * This value is set to true if the platform is a standard graphical libGDX backend. False otherwise.
   */
  val isStandard: Boolean
    get() = true

  /**
   * Creates a new gradle file used to manage this project's dependencies.
   * @param project requests the creation of file.
   */
  fun createGradleFile(project: Project): GradleFile

  /**
   * This method is used to resolve additional dependencies in other projects.
   * @param project contains the platform.
   */
  fun initiate(project: Project)

  fun addCopiedFile(project: Project, vararg file: String) {
    val originalFile = arrayOf("generator", id) + file
    project.files.add(CopiedFile(projectName = id, original = path(*originalFile), path = path(*file)))
  }

  fun addGeneratedImageFile(project: Project, content: Pixmap, vararg file: String) {
    project.files.add(GeneratedImageFile(projectName = id, content = content, path = path(*file)))
  }

  fun addGradleTaskDescription(project: Project, task: String, description: String) {
    project.addGradleTaskDescription("$id:$task", description)
  }
}
