package gdx.liftoff.data.templates.unofficial

import gdx.liftoff.data.libs.unofficial.Kiwi
import gdx.liftoff.data.project.Project
import gdx.liftoff.data.templates.official.ClassicTemplate
import gdx.liftoff.views.ProjectTemplate

/**
 * Classic project template using Kiwi utilities.
 */
@ProjectTemplate
@Suppress("unused") // Referenced via reflection.
class KiwiTemplate : ClassicTemplate() {
	override val id = "kiwiTemplate"
	private lateinit var mainClass: String
	override val width: String
		get() = mainClass + ".WIDTH"
	override val height: String
		get() = mainClass + ".HEIGHT"
	override val description: String
		get() = "Project template included simple launchers and an `AbstractApplicationListener` extension (from [Kiwi](https://github.com/czyzby/gdx-lml/tree/master/kiwi) library) that draws BadLogic logo."

	override fun apply(project: Project) {
		mainClass = project.basic.mainClass
		super.apply(project)
		// Adding gdx-kiwi dependency:
		Kiwi().initiate(project)
	}

	override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.kiwi.util.gdx.AbstractApplicationListener;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.scene2d.Actors;
import com.github.czyzby.kiwi.util.gdx.viewport.LetterboxingViewport;
import com.github.czyzby.kiwi.util.gdx.viewport.Viewports;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ${project.basic.mainClass} extends AbstractApplicationListener {
	/** Default application size. */
	public static final int WIDTH = 640, HEIGHT = 480;

	private Stage stage;
	private Texture texture;
	private Image image;

	@Override
	public void create() {
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
		// AbstractApplicationListener automatically clears the screen with black color.
		stage.act(deltaTime);
		stage.draw();
	}

	@Override
	public void dispose() {
		// Null-safe disposing utility method:
		Disposables.disposeOf(stage, texture);
	}
}"""
}
