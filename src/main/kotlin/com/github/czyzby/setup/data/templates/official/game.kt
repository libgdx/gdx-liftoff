package com.github.czyzby.setup.data.templates.official

import com.github.czyzby.setup.data.platforms.Core
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.data.templates.Template
import com.github.czyzby.setup.views.ProjectTemplate

/**
 * Uses Game as ApplicationListener. Provides an example (empty) Screen implementation.
 * @author MJ
 */
@ProjectTemplate(official = true)
class GameTemplate : Template {
    override val id = "gameTemplate"
    override val description: String
        get() = "Project template includes simple launchers and a `Game` extension that sets the first screen."

    override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ${project.basic.mainClass} extends Game {
	@Override
	public void create() {
		setScreen(new FirstScreen());
	}
}"""

	override fun apply(project: Project) {
		super.apply(project)
		addSourceFile(project = project, platform = Core.ID, packageName = project.basic.rootPackage,
				fileName = "FirstScreen.java", content = """package ${project.basic.rootPackage};

import com.badlogic.gdx.Screen;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
	@Override
	public void show() {
		// Prepare your screen here.
	}

	@Override
	public void render(float delta) {
		// Draw your screen here. "delta" is the time since last render in seconds.
	}

	@Override
	public void resize(int width, int height) {
		// Resize your screen here. The parameters represent the new window size.
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
	public void hide() {
		// This method is called when another screen replaces this one.
	}

	@Override
	public void dispose() {
		// Destroy screen's assets here.
	}
}""")
	}
}
