package com.github.czyzby.setup.data.templates.official

import com.github.czyzby.setup.data.files.CopiedFile
import com.github.czyzby.setup.data.files.path
import com.github.czyzby.setup.data.platforms.Assets
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.data.templates.Template
import com.github.czyzby.setup.views.ProjectTemplate

/**
 * Draws Badlogic Games logo at the center of the screen.
 */
@ProjectTemplate(official = true)
open class ClassicTemplate : Template {
    override val id = "classic"
    override val description: String
        get() = "Project template includes simple launchers and an `ApplicationAdapter` extension that draws BadLogic logo."

    override fun apply(project: Project) {
        super.apply(project)
        project.files.add(CopiedFile(projectName = Assets.ID, original = path("generator", "templates", "classic",
                "badlogic.png"), path = "badlogic.png"))
    }

    override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ${project.basic.mainClass} extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture image;

	@Override
	public void create() {
		batch = new SpriteBatch();
		image = new Texture("badlogic.png");
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(image, 165, 180);
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		image.dispose();
	}
}"""
}
