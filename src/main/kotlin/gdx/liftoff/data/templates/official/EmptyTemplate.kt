package gdx.liftoff.data.templates.official

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.project.Project
import gdx.liftoff.data.templates.Template
import gdx.liftoff.views.ProjectTemplate

/**
 * Generates no source files.
 */
@ProjectTemplate(official = true)
@Suppress("unused") // Referenced via reflection.
class EmptyTemplate : Template {
  override val id = "emptyTemplate"
  override val description: String
    get() = "This project was generated without an `ApplicationListener` implementation."

  override fun apply(project: Project) {
    super.apply(project)
    project.files.add(
      CopiedFile(
        projectName = Assets.ID,
        original = path("generator", "assets", ".gitkeep"),
        path = ".gitkeep"
      )
    )
  }

  override fun getApplicationListenerContent(project: Project): String = ""
}
