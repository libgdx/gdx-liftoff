package gdx.liftoff.data.templates.unofficial

import gdx.liftoff.data.files.SourceFile
import gdx.liftoff.data.libraries.unofficial.LML
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.project.Project
import gdx.liftoff.data.templates.Template
import gdx.liftoff.views.ProjectTemplate

/**
 * Uses LML to generate GUI similar to the one presented in the official Scene2D template. Forces skin generation.
 */
@ProjectTemplate
@Suppress("unused") // Referenced via reflection.
class LmlTemplate : Template {
  override val id: String = "lmlTemplate"
  private lateinit var mainClass: String
  override val width: String
    get() = "$mainClass.WIDTH"
  override val height: String
    get() = "$mainClass.HEIGHT"
  override val description = "Project template included simple launchers and an `AbstractApplicationListener` " +
    "extension (from [Kiwi](https://github.com/crashinvaders/gdx-lml/tree/master/kiwi) library) that draws " +
    "a simple GUI created using [LML](https://github.com/crashinvaders/gdx-lml/tree/master/lml)."

  override fun apply(project: Project) {
    mainClass = project.basic.mainClass
    super.apply(project)
    project.advanced.generateSkin = true

    // Adding LML dependency:
    LML().initiate(project)

    // Adding example LML template file:
    project.files.add(
      SourceFile(
        projectName = Assets.ID,
        sourceFolderPath = "ui",
        packageName = "templates",
        fileName = "main.lml",
        content = """<!-- Note: you can get content assist thanks to DTD schema files. See the official LML page. -->
<window title="Example" style="border" defaultPad="4" oneColumn="true" alpha="0" onShow="fadeIn">
  This is a simple LML view.
  <textButton onClick="setClicked" tablePad="8">Click me!</textButton>
</window>"""
      )
    )
  }

  override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.czyzby.kiwi.util.gdx.AbstractApplicationListener;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.util.Lml;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ${project.basic.mainClass} extends AbstractApplicationListener {
    /** Default application size. */
    public static final int WIDTH = 640, HEIGHT = 480;

    private Stage stage;
    private Skin skin;

    @Override
    public void create() {
        stage = new Stage(new FitViewport(WIDTH, HEIGHT));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        LmlParser parser = Lml.parser(skin)
            // Adding action for the button listener:
            .action("setClicked", new ActorConsumer<Void, TextButton>() {
                @Override public Void consume(TextButton actor) {
                    actor.setText("Clicked."); return null;
                }
            })
            // Adding showing action for the window:
            .action("fadeIn", new ActorConsumer<Action, Window>() {
                @Override public Action consume(Window actor) {
                    return Actions.fadeIn(1f);
                }
            }).build();

        // Parsing actors defined in main.lml template and adding them to stage:
        parser.fillStage(stage, Gdx.files.internal("ui/templates/main.lml"));
        // Note: there are less verbose and more powerful ways of using LML. See other LML project templates.

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
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
        Disposables.disposeOf(stage, skin);
    }
}"""
}
