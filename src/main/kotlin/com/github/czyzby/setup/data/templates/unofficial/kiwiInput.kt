package com.github.czyzby.setup.data.templates.unofficial

import com.github.czyzby.setup.data.libs.unofficial.Kiwi
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.data.templates.official.ClassicTemplate
import com.github.czyzby.setup.views.ProjectTemplate

/**
 * Classic project template using Kiwi utilities and listening to user input.
 * @author MJ
 */
@ProjectTemplate
class KiwiInputTemplate : ClassicTemplate(){
	override val id = "kiwiInputTemplate"
	private lateinit var mainClass: String
	override val width: String
		get() = mainClass + ".WIDTH"
	override val height: String
		get() = mainClass + ".HEIGHT"
	override val description: String
		get() = "Project template included simple launchers and an `InputAwareApplicationListener` extension (from [Kiwi](https://github.com/czyzby/gdx-lml/tree/master/kiwi) library) that draws BadLogic logo and changes its color after the application is clicked/touched."

	override fun apply(project: Project) {
		mainClass = project.basic.mainClass
		super.apply(project)
		// Adding gdx-kiwi dependency:
		Kiwi().initiate(project)
	}

	override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.kiwi.util.gdx.InputAwareApplicationListener;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.collection.immutable.ImmutableArray;
import com.github.czyzby.kiwi.util.gdx.scene2d.Actors;
import com.github.czyzby.kiwi.util.gdx.viewport.LetterboxingViewport;
import com.github.czyzby.kiwi.util.gdx.viewport.Viewports;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ${project.basic.mainClass} extends InputAwareApplicationListener {
	/** Default application size. */
	public static final int WIDTH = 640, HEIGHT = 480;

	// Static Kiwi utility - creates an immutable array of colors:
	private final Array<Color> colors = ImmutableArray.of(Color.WHITE, Color.RED, Color.GREEN,
			Color.BLUE, Color.ORANGE, Color.PURPLE, Color.PINK, Color.SKY, Color.SCARLET);

	private Stage stage;
	private Texture texture;
	private Image image;

	@Override
	public void initiate() {
		stage = new Stage(new LetterboxingViewport());
		texture = new Texture("badlogic.png");
		image = new Image(texture);
		stage.addActor(image);
		Actors.centerActor(image);
	}

	@Override
	public void resize(int width, int height) {
		Viewports.update(stage);
		Actors.centerActor(image);
	}

	@Override
	public void render(float deltaTime) {
		// InputAwareApplicationListener automatically clears the screen with black color.
		stage.act(deltaTime);
		stage.draw();
	}

	@Override
	public void dispose() {
		// Null-safe disposing utility method:
		Disposables.disposeOf(stage, texture);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// Changing current logo color when the screen is clicked/touched:
		image.setColor(colors.random());
		return true;
	}
}"""
}
