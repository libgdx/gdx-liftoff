package gdx.liftoff.data.templates.official

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.project.Project
import gdx.liftoff.data.templates.Template
import gdx.liftoff.views.ProjectTemplate

/**
 * Extends InputAdapter, overriding no methods. Sets itself as the input processor.
 */
@ProjectTemplate(official = true)
@Suppress("unused") // Referenced via reflection.
class InputProcessorTemplate : Template {
  override val id = "inputProcessor"
  override val description: String
    get() = "This project was generated with a template including simple application launchers and " +
      "an `ApplicationListener` implementation that listens to user input."

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

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. Listens to user input. */
public class ${project.basic.mainClass} extends InputAdapter implements ApplicationListener {
    @Override
    public void create() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void resize(final int width, final int height) {
    }

    @Override
    public void render() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

    // Note: you can override methods from InputAdapter API to handle user's input.
}"""
}
