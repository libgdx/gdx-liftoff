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
class KotlinClassicTemplate : KotlinTemplate {
  override val id = "kotlinClassicTemplate"
  override val description = "This project was generated with a template that includes Kotlin application " +
    "launchers and draws the libGDX logo within the application listener."

  override fun apply(project: Project) {
    super.apply(project)
    project.files.add(
      CopiedFile(
        projectName = Assets.ID,
        original = path("generator", "templates", "libgdx.png"),
        path = "libgdx.png"
      )
    )
  }

  override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage}

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms. */
class ${project.basic.mainClass} : ApplicationAdapter() {
    private val batch by lazy { SpriteBatch() }
    private val image by lazy { Texture("libgdx.png") }

    override fun render() {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        batch.draw(image, 140f, 210f)
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        image.dispose()
    }
}
"""
}
