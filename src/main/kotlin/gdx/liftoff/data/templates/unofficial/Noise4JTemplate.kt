package gdx.liftoff.data.templates.unofficial

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.libraries.unofficial.Noise4J
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.project.Project
import gdx.liftoff.data.templates.Template
import gdx.liftoff.views.ProjectTemplate

@ProjectTemplate
@Suppress("unused") // Referenced via reflection.
class Noise4JTemplate : Template {
  override val id = "noise4jTemplate"
  private lateinit var mainClass: String
  override val width: String
    get() = "$mainClass.SIZE * 2"
  override val height: String
    get() = "$mainClass.SIZE * 2"
  override val description: String
    get() = "Project template included simple launchers and an `ApplicationAdapter` extension that draws random " +
      "maps created using [Noise4J](https://github.com/czyzby/noise4j)."

  override fun apply(project: Project) {
    mainClass = project.basic.mainClass
    super.apply(project)
    project.files.add(
      CopiedFile(
        projectName = Assets.ID,
        original = path("generator", "assets", ".gitkeep"),
        path = ".gitkeep"
      )
    )
    // Including noise4j dependency:
    Noise4J().initiate(project)
  }

  override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.cellular.CellularAutomataGenerator;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;
import com.github.czyzby.noise4j.map.generator.room.RoomType.DefaultRoomType;
import com.github.czyzby.noise4j.map.generator.room.dungeon.DungeonGenerator;
import com.github.czyzby.noise4j.map.generator.util.Generators;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ${project.basic.mainClass} extends ApplicationAdapter {
    /** Size of the generated maps. */
    public static final int SIZE = 200;

    private Grid grid = new Grid(SIZE);
    private Batch batch;
    private Texture texture;
    private Pixmap pixmap;

    @Override
    public void create() {
        pixmap = new Pixmap(SIZE, SIZE, Format.RGBA8888);
        batch = new SpriteBatch();
        // Adding event listener - recreating map on click:
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                rollMap();
                return true;
            }
        });
        // Creating a random map:
        rollMap();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(texture, 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        texture.dispose();
        pixmap.dispose();
    }

    private void rollMap() {
        // Clearing all grid values:
        grid.set(0f);
        // Choosing map generator:
        float test = MathUtils.random();
        if (test < 0.25f) {
            createNoiseMap();
        } else if (test < 0.50f) {
            createCellularMap();
        } else if (test < 0.75f) {
            createSimpleDungeonMap();
        } else {
            createDungeonMap();
        }

        createTexture();
    }

    /** Uses NoiseGenerator to create a height map. */
    private void createNoiseMap() {
        NoiseGenerator noiseGenerator = new NoiseGenerator();
        // The first value is the radius, the second is the modifier. Ensuring that the biggest regions have the highest
        // modifier allows to generate interesting maps with smooth transitions between regions.
        noiseStage(noiseGenerator, 32, 0.45f);
        noiseStage(noiseGenerator, 16, 0.25f);
        noiseStage(noiseGenerator, 8, 0.15f);
        noiseStage(noiseGenerator, 4, 0.1f);
        noiseStage(noiseGenerator, 2, 0.05f);
    }

    private void noiseStage(NoiseGenerator noiseGenerator, int radius, float modifier) {
        noiseGenerator.setRadius(radius); // Radius of a single sector.
        noiseGenerator.setModifier(modifier); // The max value added to a single cell.
        // Seed ensures randomness, can be saved if you feel the need to generate the same map in the future.
        noiseGenerator.setSeed(Generators.rollSeed());
        noiseGenerator.generate(grid);
    }

    /** Uses CellularAutomataGenerator to create a cave-like map. */
    private void createCellularMap() {
        CellularAutomataGenerator cellularGenerator = new CellularAutomataGenerator();
        cellularGenerator.setAliveChance(0.5f); // 50% of cells will start as filled.
        cellularGenerator.setIterationsAmount(4); // The more iterations, the smoother the map.
        cellularGenerator.generate(grid);
    }

    /** Uses DungeonGenerator to create a simple wall-corridor-room type of map. */
    private void createSimpleDungeonMap() {
        DungeonGenerator dungeonGenerator = new DungeonGenerator();
        dungeonGenerator.setRoomGenerationAttempts(500); // The bigger it is, the more rooms are likely to appear.
        dungeonGenerator.setMaxRoomSize(21); // Max room size, should be odd.
        dungeonGenerator.setTolerance(5); // Max difference between width and height.
        dungeonGenerator.setMinRoomSize(5); // Min room size, should be odd.
        dungeonGenerator.generate(grid);
    }

    /** Uses DungeonGenerator to create a wall-corridor-room type of map with different room types. */
    private void createDungeonMap() {
        DungeonGenerator dungeonGenerator = new DungeonGenerator();
        dungeonGenerator.setRoomGenerationAttempts(500); // The bigger it is, the more rooms are likely to appear.
        dungeonGenerator.setMaxRoomSize(25); // Max room size, should be odd.
        dungeonGenerator.setTolerance(5); // Max difference between width and height.
        dungeonGenerator.setMinRoomSize(9); // Min room size, should be odd.
        dungeonGenerator.addRoomTypes(DefaultRoomType.values()); // Adding different room types.
        dungeonGenerator.generate(grid);
    }

    private void createTexture() {
        // Destroying previous texture:
        if (texture != null) {
            texture.dispose();
        }
        // Drawing on pixmap according to grid's values:
        Color color = new Color();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                float cell = grid.get(x, y);
                color.set(cell, cell, cell, 1f);
                pixmap.drawPixel(x, y, Color.rgba8888(color));
            }
        } // Creating a new texture with the values from pixmap:
        texture = new Texture(pixmap);
    }
}"""
}
