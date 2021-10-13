package com.github.czyzby.setup.data.templates.unofficial

import com.github.czyzby.setup.data.files.CopiedFile
import com.github.czyzby.setup.data.files.path
import com.github.czyzby.setup.data.libs.unofficial.VisUI
import com.github.czyzby.setup.data.platforms.Assets
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.data.templates.Template
import com.github.czyzby.setup.views.ProjectTemplate

/**
 * Simple VisUI example showing how to actually include VisUI in user project
 * @author Kotcrab
 */
@ProjectTemplate
@Suppress("unused") // Referenced via reflection.
class VisUIBasicTemplate : Template {
	override val id = "visUiBasicTemplate"
	override val description: String
		get() = "Project template included simple launchers and an `ApplicationAdapter` extension with GUI created using [VisUI](https://github.com/kotcrab/VisEditor/wiki/VisUI) library."

	override fun apply(project: Project) {
		super.apply(project)
		project.files.add(
			CopiedFile(projectName = Assets.ID, original = path("generator", "assets",
				".gitkeep"), path = ".gitkeep")
		)
		VisUI().initiate(project)
	}

	override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ${project.basic.mainClass} extends ApplicationAdapter {
	private Stage stage;

	@Override
	public void create () {
		VisUI.setSkipGdxVersionCheck(true);
		VisUI.load(SkinScale.X1);

		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		VisTable root = new VisTable();
		root.setFillParent(true);
		stage.addActor(root);

		final VisTextButton textButton = new VisTextButton("click me!");
		textButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				textButton.setText("clicked");
				Dialogs.showOKDialog(stage, "message", "good job!");
			}
		});

		VisWindow window = new VisWindow("example window");
		window.add("this is a simple VisUI window").padTop(5f).row();
		window.add(textButton).pad(10f);
		window.pack();
		window.centerWindow();
		stage.addActor(window.fadeIn());
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
	}

	@Override
	public void dispose () {
		VisUI.dispose();
		stage.dispose();
	}
}"""
}
