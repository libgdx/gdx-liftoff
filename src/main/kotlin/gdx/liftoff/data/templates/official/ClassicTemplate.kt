package gdx.liftoff.data.templates.official

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.project.Project
import gdx.liftoff.data.templates.Template
import gdx.liftoff.views.ProjectTemplate

/**
 * Draws libGDX logo at the center of the screen.
 */
@ProjectTemplate(official = true)
open class ClassicTemplate : Template {
  override val id = "classic"
  override val description: String
    get() =
      "This project was generated with a template including simple application launchers and an " +
        "`ApplicationAdapter` extension that draws libGDX logo."

  override fun apply(project: Project) {
    super.apply(project)
    project.files.add(
      CopiedFile(
        projectName = Assets.ID,
        original = path("generator", "templates", "libgdx.png"),
        path = "libgdx.png",
      ),
    )
  }

  override fun getApplicationListenerContent(project: Project): String =
    """package ${project.basic.rootPackage};

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ${project.basic.mainClass} extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
"""
}
