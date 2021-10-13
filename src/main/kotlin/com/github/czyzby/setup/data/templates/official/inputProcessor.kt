package com.github.czyzby.setup.data.templates.official

import com.github.czyzby.setup.data.files.CopiedFile
import com.github.czyzby.setup.data.files.path
import com.github.czyzby.setup.data.platforms.Assets
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.data.templates.Template
import com.github.czyzby.setup.views.ProjectTemplate

/**
 * Extends InputAdapter, overriding no methods. Sets itself as the input processor.
 */
@ProjectTemplate(official = true)
@Suppress("unused") // Referenced via reflection.
class InputProcessorTemplate : Template {
    override val id = "inputProcessor"
    override val description: String
        get() = "Project template included simple launchers and an empty `ApplicationListener` implementation, that also listened to user input."

    override fun apply(project: Project) {
        super.apply(project)
        project.files.add(
            CopiedFile(projectName = Assets.ID, original = path("generator", "assets",
                ".gitkeep"), path = ".gitkeep")
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
