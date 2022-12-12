package gdx.liftoff.data.templates.unofficial

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.libraries.unofficial.KtxApp
import gdx.liftoff.data.libraries.unofficial.KtxAssets
import gdx.liftoff.data.libraries.unofficial.KtxAssetsAsync
import gdx.liftoff.data.libraries.unofficial.KtxAsync
import gdx.liftoff.data.libraries.unofficial.KtxFreetypeAsync
import gdx.liftoff.data.libraries.unofficial.KtxGraphics
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.project.Project
import gdx.liftoff.data.templates.KotlinTemplate
import gdx.liftoff.views.ProjectTemplate

/**
 * Basic KTX template. Written in Kotlin. Uses KTX utilities to draw KTX logo.
 */
@ProjectTemplate
@Suppress("unused") // Referenced via reflection.
class KtxTemplate : KotlinTemplate {
  override val id = "ktxTemplate"
  override val description = "This project was generated with a Kotlin project template that includes Kotlin " +
    "application launchers and [KTX](https://libktx.github.io/) utilities."

  override fun apply(project: Project) {
    super.apply(project)

    KtxApp().initiate(project)
    KtxGraphics().initiate(project)
    KtxAssets().initiate(project)

    project.files.add(
      CopiedFile(
        projectName = Assets.ID,
        original = path("generator", "templates", "ktx", "ktx.png"),
        path = "logo.png"
      )
    )
  }

  private val Project.isUsingAsync: Boolean
    get() = listOf(KtxAsync(), KtxAssetsAsync(), KtxFreetypeAsync()).map { it.id }.any(extensions::isSelected)

  override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage}

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile${
  if (project.isUsingAsync) "\nimport ktx.async.KtxAsync" else ""
  }
import ktx.graphics.use

class ${project.basic.mainClass} : KtxGame<KtxScreen>() {
    override fun create() {${
  if (project.isUsingAsync) "\n        KtxAsync.initiate()\n" else ""
  }
        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    private val image = Texture("logo.png".toInternalFile(), true).apply { setFilter(Linear, Linear) }
    private val batch = SpriteBatch()

    override fun render(delta: Float) {
        clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)
        batch.use {
            it.draw(image, 100f, 160f)
        }
    }

    override fun dispose() {
        image.disposeSafely()
        batch.disposeSafely()
    }
}
"""
}
