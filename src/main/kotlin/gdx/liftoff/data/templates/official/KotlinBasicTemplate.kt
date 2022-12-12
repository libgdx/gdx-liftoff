package gdx.liftoff.data.templates.official

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.project.Project
import gdx.liftoff.data.templates.KotlinTemplate
import gdx.liftoff.views.ProjectTemplate

/**
 * Written in Kotlin. Includes Kotlin launchers for each platform.
 * Extends ApplicationAdapter, overriding no methods. Application does nothing.
 */
@ProjectTemplate(official = true)
@Suppress("unused") // Referenced via reflection.
class KotlinBasicTemplate : KotlinTemplate {
  override val id = "kotlinTemplate"
  override val description = "This project was generated with a template that includes Kotlin application " +
    "launchers and an empty `ApplicationAdapter` implemented in Kotlin."

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

  override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage}

import com.badlogic.gdx.ApplicationAdapter

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms. */
class ${project.basic.mainClass} : ApplicationAdapter()
"""
}
