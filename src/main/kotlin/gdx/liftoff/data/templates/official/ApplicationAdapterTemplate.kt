package gdx.liftoff.data.templates.official

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.project.Project
import gdx.liftoff.data.templates.Template
import gdx.liftoff.views.ProjectTemplate

/**
 * Extends ApplicationAdapter, overriding no methods. Application does nothing.
 */
@ProjectTemplate(official = true)
@Suppress("unused") // Referenced via reflection.
class ApplicationAdapterTemplate : Template {
  override val id = "applicationAdapter"
  override val description: String
    get() = "This project was generated with a template including simple application launchers and an empty " +
      "`ApplicationAdapter` extension."

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

  override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

import com.badlogic.gdx.ApplicationAdapter;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ${project.basic.mainClass} extends ApplicationAdapter {
}"""
}
