package gdx.liftoff.data.templates.official

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.project.Project
import gdx.liftoff.data.templates.Template
import gdx.liftoff.views.ProjectTemplate

/**
 * Adds empty implementation of all ApplicationListener methods. Application does nothing.
 */
@ProjectTemplate(official = true)
@Suppress("unused") // Referenced via reflection.
class ApplicationListenerTemplate : Template {
  override val id = "applicationListener"
  override val description: String
    get() =
      "This project was generated with a template including simple application launchers and an empty " +
        "`ApplicationListener` implementation."

  override fun apply(project: Project) {
    super.apply(project)
    project.files.add(
      CopiedFile(
        projectName = Assets.ID,
        original = path("generator", "assets", ".gitkeep"),
        path = ".gitkeep",
      ),
    )
  }

  override fun getApplicationListenerContent(project: Project): String =
    """package ${project.basic.rootPackage};

import com.badlogic.gdx.ApplicationListener;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ${project.basic.mainClass} implements ApplicationListener {
    @Override
    public void create() {
        // Prepare your application here.
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your application here. The parameters represent the new window size.
    }

    @Override
    public void render() {
        // Draw your application here.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}"""
}
