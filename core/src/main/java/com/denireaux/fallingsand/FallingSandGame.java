package com.denireaux.fallingsand;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.denireaux.fallingsand.particletypes.AshParticle;
import com.denireaux.fallingsand.particletypes.CarbonParticle;
import com.denireaux.fallingsand.particletypes.LavaParticle;
import com.denireaux.fallingsand.particletypes.Particle;
import com.denireaux.fallingsand.particletypes.PowderParticle;
import com.denireaux.fallingsand.particletypes.SandParticle;
import com.denireaux.fallingsand.particletypes.SmokeParticle;
import com.denireaux.fallingsand.particletypes.SnowParticle;
import com.denireaux.fallingsand.particletypes.StoneParticle;
import com.denireaux.fallingsand.particletypes.VaporParticle;
import com.denireaux.fallingsand.particletypes.VoidParticle;
import com.denireaux.fallingsand.particletypes.WaterParticle;
import com.denireaux.fallingsand.particletypes.WetSandParticle;

public class FallingSandGame extends ApplicationAdapter {
    private static final Logger log = Logger.getLogger(String.valueOf(FallingSandGame.class));

    private OrthographicCamera camera;
    private Viewport viewport;
    private final Vector3 worldCoordinates = new Vector3();

    // Increased resolution
    public static final int GRID_WIDTH = 1500;
    public static final int GRID_HEIGHT = 1000;
    public static final int CELL_SIZE = 1;

    private SpriteBatch batch;
    private Texture pixel;
    private BitmapFont font;
    private static Particle[][] grid;

    // Render modes
    private enum RenderStyle { PIXEL, TEXTURE }
    private RenderStyle currentStyle = RenderStyle.PIXEL;

    // Particle types
    private enum ParticleType {
        SAND, WATER, WETSAND, VAPOR, LAVA,
        STONE, ASH, POWDER, SMOKE, SNOW,
        CARBON, VOID
    }

    private ParticleType currentParticle = ParticleType.SAND;
    private int particleIndex = 0;
    private final ParticleType[] particleTypes = ParticleType.values();

    // Palette buttons (sidebar)
    private Rectangle[] paletteButtons;

    // Button colors
    private final Color SANDCOLOR = Color.YELLOW;
    private final Color WATERCOLOR = Color.BLUE;
    private final Color WETSANDCOLOR = Color.BROWN;
    private final Color VAPORCOLOR = Color.LIGHT_GRAY;
    private final Color LAVACOLOR = Color.RED;
    private final Color STONECOLOR = Color.DARK_GRAY;
    private final Color ASHCOLOR = Color.SLATE;
    private final Color POWDERCOLOR = Color.TAN;
    private final Color SMOKECOLOR = Color.BLACK;
    private final Color SNOWCOLOR = Color.WHITE;
    private final Color CARBONCOLOR = Color.BLACK;
    private final Color VOIDCOLOR = Color.FOREST;

    private Texture sandTex, waterTex, lavaTex, stoneTex, snowTex;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GRID_WIDTH, GRID_HEIGHT, camera);
        viewport.apply();
        camera.position.set(GRID_WIDTH / 2f, GRID_HEIGHT / 2f, 0);
        camera.update();

        Gdx.graphics.setForegroundFPS(120);
        batch = new SpriteBatch();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();

        font = new BitmapFont();
        grid = new Particle[GRID_WIDTH][GRID_HEIGHT];

        sandTex = safeLoad("sand.png");
        waterTex = safeLoad("water.png");
        lavaTex = safeLoad("lava.png");
        stoneTex = safeLoad("stone.png");
        snowTex = safeLoad("snow.png");

        // Build vertical palette on the right side
        int buttonWidth = 100;
        int buttonHeight = 30;
        int padding = 10;
        int startY = GRID_HEIGHT - buttonHeight - padding;

        paletteButtons = new Rectangle[particleTypes.length];
        for (int i = 0; i < particleTypes.length; i++) {
            paletteButtons[i] = new Rectangle(
                GRID_WIDTH - buttonWidth - padding,
                startY - i * (buttonHeight + padding),
                buttonWidth,
                buttonHeight
            );
        }

        // Setup scroll wheel handler
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                if (amountY > 0) { // scroll down
                    particleIndex = (particleIndex + 1) % particleTypes.length;
                } else if (amountY < 0) { // scroll up
                    particleIndex = (particleIndex - 1 + particleTypes.length) % particleTypes.length;
                }
                currentParticle = particleTypes[particleIndex];
                log.info("Selected particle: " + currentParticle);
                return true;
            }
        });
    }

    private Texture safeLoad(String fileName) {
        try {
            return new Texture(Gdx.files.internal(fileName));
        } catch (Exception e) {
            log.log(Level.WARNING, "Missing texture: {0}", fileName);
            return null;
        }
    }

    @Override
    public void render() {
        handleInput();

        float r = 29f / 255f, g = 31f / 255f, b = 33f / 255f;
        Gdx.gl.glClearColor(r, g, b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update particles
        for (int y = GRID_HEIGHT - 1; y >= 0; y--) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (grid[x][y] != null) {
                    grid[x][y].update(0.1f, grid);
                }
            }
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Render particles
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (grid[x][y] instanceof SandParticle) {
                    batch.setColor(SANDCOLOR);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof WaterParticle) {
                    batch.setColor(WATERCOLOR);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof WetSandParticle) {
                    batch.setColor(WETSANDCOLOR);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof VaporParticle) {
                    batch.setColor(VAPORCOLOR);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof LavaParticle) {
                    batch.setColor(LAVACOLOR);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof StoneParticle) {
                    batch.setColor(STONECOLOR);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof AshParticle) {
                    batch.setColor(ASHCOLOR);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof PowderParticle) {
                    batch.setColor(POWDERCOLOR);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof SmokeParticle) {
                    batch.setColor(SMOKECOLOR);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof SnowParticle) {
                    batch.setColor(SNOWCOLOR);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof CarbonParticle) {
                    batch.setColor(CARBONCOLOR);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof VoidParticle) {
                    batch.setColor(VOIDCOLOR);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        // Render palette sidebar
        for (int i = 0; i < particleTypes.length; i++) {
            ParticleType type = particleTypes[i];
            Color color = getColorForType(type);
            drawButton(paletteButtons[i], color, type.name(), type);
        }

        batch.end();
    }

    private Color getColorForType(ParticleType type) {
        return switch (type) {
            case SAND -> SANDCOLOR;
            case WATER -> WATERCOLOR;
            case WETSAND -> WETSANDCOLOR;
            case VAPOR -> VAPORCOLOR;
            case LAVA -> LAVACOLOR;
            case STONE -> STONECOLOR;
            case ASH -> ASHCOLOR;
            case POWDER -> POWDERCOLOR;
            case SMOKE -> SMOKECOLOR;
            case SNOW -> SNOWCOLOR;
            case CARBON -> CARBONCOLOR;
            case VOID -> VOIDCOLOR;
        };
    }

    private void drawButton(Rectangle rect, Color color, String label, ParticleType type) {
        batch.setColor(color);
        batch.draw(pixel, rect.x, rect.y, rect.width, rect.height);

        if (currentParticle == type) {
            batch.setColor(Color.WHITE);
            batch.draw(pixel, rect.x - 2, rect.y + rect.height, rect.width + 4, 2);
            batch.draw(pixel, rect.x - 2, rect.y - 2, rect.width + 4, 2);
            batch.draw(pixel, rect.x - 2, rect.y - 2, 2, rect.height + 4);
            batch.draw(pixel, rect.x + rect.width, rect.y - 2, 2, rect.height + 4);
        }

        font.setColor(Color.WHITE);
        font.draw(batch, label, rect.x + 5, rect.y + rect.height / 1.5f);
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            currentStyle = (currentStyle == RenderStyle.PIXEL) ? RenderStyle.TEXTURE : RenderStyle.PIXEL;
            log.info("Switched render style to " + currentStyle);
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            worldCoordinates.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(worldCoordinates);

            int mouseX = (int)(worldCoordinates.x / CELL_SIZE);
            int mouseY = (int)(worldCoordinates.y / CELL_SIZE);

            int brushRadius = 9;
            for (int dx = -brushRadius; dx <= brushRadius; dx++) {
                for (int dy = -brushRadius; dy <= brushRadius; dy++) {
                    if (dx * dx + dy * dy <= brushRadius * brushRadius) {
                        int x = mouseX + dx;
                        int y = mouseY + dy;

                        if (x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT && grid[x][y] == null) {
                            switch (currentParticle) {
                                case SAND -> grid[x][y] = new SandParticle(x, y, "sand");
                                case WATER -> grid[x][y] = new WaterParticle(x, y, "water");
                                case WETSAND -> grid[x][y] = new WetSandParticle(x, y, "wetsand");
                                case VAPOR -> grid[x][y] = new VaporParticle(x, y, "vapor");
                                case LAVA -> grid[x][y] = new LavaParticle(x, y, "lava");
                                case STONE -> grid[x][y] = new StoneParticle(x, y, "stone");
                                case ASH -> grid[x][y] = new AshParticle(x, y, "ash");
                                case POWDER -> grid[x][y] = new PowderParticle(x, y, "powder");
                                case SMOKE -> grid[x][y] = new SmokeParticle(x, y, "smoke");
                                case SNOW -> grid[x][y] = new SnowParticle(x, y, "snow");
                                case CARBON -> grid[x][y] = new CarbonParticle(x, y, "carbon");
                                case VOID -> grid[x][y] = new VoidParticle(x, y, "void");
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        pixel.dispose();
        font.dispose();
        if (sandTex != null) sandTex.dispose();
        if (waterTex != null) waterTex.dispose();
        if (lavaTex != null) lavaTex.dispose();
        if (stoneTex != null) stoneTex.dispose();
        if (snowTex != null) snowTex.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(GRID_WIDTH / 2f, GRID_HEIGHT / 2f, 0);
        camera.update();
    }
}


// Below is a more stringently multithreaded way of runnning the program,
// Likely will be implemented in the case that processing logic needs to 
// Be offloaded
// God help you the day you need to do this...

// package com.denireaux.fallingsand;

// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.TimeUnit;
// import java.util.logging.Level;
// import java.util.logging.Logger;

// import com.badlogic.gdx.ApplicationAdapter;
// import com.badlogic.gdx.Gdx;
// import com.badlogic.gdx.Input;
// import com.badlogic.gdx.InputAdapter;
// import com.badlogic.gdx.graphics.Color;
// import com.badlogic.gdx.graphics.GL20;
// import com.badlogic.gdx.graphics.OrthographicCamera;
// import com.badlogic.gdx.graphics.Pixmap;
// import com.badlogic.gdx.graphics.Texture;
// import com.badlogic.gdx.graphics.g2d.BitmapFont;
// import com.badlogic.gdx.graphics.g2d.SpriteBatch;
// import com.badlogic.gdx.math.Rectangle;
// import com.badlogic.gdx.math.Vector3;
// import com.badlogic.gdx.utils.viewport.FitViewport;
// import com.badlogic.gdx.utils.viewport.Viewport;
// import com.denireaux.fallingsand.particletypes.AshParticle;
// import com.denireaux.fallingsand.particletypes.CarbonParticle;
// import com.denireaux.fallingsand.particletypes.LavaParticle;
// import com.denireaux.fallingsand.particletypes.Particle;
// import com.denireaux.fallingsand.particletypes.PowderParticle;
// import com.denireaux.fallingsand.particletypes.SandParticle;
// import com.denireaux.fallingsand.particletypes.SmokeParticle;
// import com.denireaux.fallingsand.particletypes.SnowParticle;
// import com.denireaux.fallingsand.particletypes.StoneParticle;
// import com.denireaux.fallingsand.particletypes.VaporParticle;
// import com.denireaux.fallingsand.particletypes.VoidParticle;
// import com.denireaux.fallingsand.particletypes.WaterParticle;
// import com.denireaux.fallingsand.particletypes.WetSandParticle;

// public class FallingSandGame extends ApplicationAdapter {
//     private static final Logger log = Logger.getLogger(String.valueOf(FallingSandGame.class));

//     private OrthographicCamera camera;
//     private Viewport viewport;
//     private final Vector3 worldCoordinates = new Vector3();

//     // Resolution
//     public static final int GRID_WIDTH = 1500;
//     public static final int GRID_HEIGHT = 1000;
//     public static final int CELL_SIZE = 1;

//     private SpriteBatch batch;
//     private Texture pixel;
//     private BitmapFont font;
//     private static Particle[][] grid;
//     private static Particle[][] nextGrid;

//     // Threading
//     private final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
//     private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

//     // Render modes
//     private enum RenderStyle { PIXEL, TEXTURE }
//     private RenderStyle currentStyle = RenderStyle.PIXEL;

//     // Particle types
//     private enum ParticleType {
//         SAND, WATER, WETSAND, VAPOR, LAVA,
//         STONE, ASH, POWDER, SMOKE, SNOW,
//         CARBON, VOID
//     }

//     private ParticleType currentParticle = ParticleType.SAND;
//     private int particleIndex = 0;
//     private final ParticleType[] particleTypes = ParticleType.values();

//     // Palette buttons (sidebar)
//     private Rectangle[] paletteButtons;

//     // Button colors
//     private final Color SANDCOLOR = Color.YELLOW;
//     private final Color WATERCOLOR = Color.BLUE;
//     private final Color WETSANDCOLOR = Color.BROWN;
//     private final Color VAPORCOLOR = Color.LIGHT_GRAY;
//     private final Color LAVACOLOR = Color.RED;
//     private final Color STONECOLOR = Color.DARK_GRAY;
//     private final Color ASHCOLOR = Color.SLATE;
//     private final Color POWDERCOLOR = Color.TAN;
//     private final Color SMOKECOLOR = Color.BLACK;
//     private final Color SNOWCOLOR = Color.WHITE;
//     private final Color CARBONCOLOR = Color.BLACK;
//     private final Color VOIDCOLOR = Color.FOREST;

//     private Texture sandTex, waterTex, lavaTex, stoneTex, snowTex;

//     @Override
//     public void create() {
//         camera = new OrthographicCamera();
//         viewport = new FitViewport(GRID_WIDTH, GRID_HEIGHT, camera);
//         viewport.apply();
//         camera.position.set(GRID_WIDTH / 2f, GRID_HEIGHT / 2f, 0);
//         camera.update();

//         Gdx.graphics.setForegroundFPS(120);
//         batch = new SpriteBatch();
//         Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
//         pixmap.setColor(Color.WHITE);
//         pixmap.fill();
//         pixel = new Texture(pixmap);
//         pixmap.dispose();

//         font = new BitmapFont();
//         grid = new Particle[GRID_WIDTH][GRID_HEIGHT];
//         nextGrid = new Particle[GRID_WIDTH][GRID_HEIGHT];

//         sandTex = safeLoad("sand.png");
//         waterTex = safeLoad("water.png");
//         lavaTex = safeLoad("lava.png");
//         stoneTex = safeLoad("stone.png");
//         snowTex = safeLoad("snow.png");

//         // Palette sidebar
//         int buttonWidth = 100;
//         int buttonHeight = 30;
//         int padding = 10;
//         int startY = GRID_HEIGHT - buttonHeight - padding;

//         paletteButtons = new Rectangle[particleTypes.length];
//         for (int i = 0; i < particleTypes.length; i++) {
//             paletteButtons[i] = new Rectangle(
//                 GRID_WIDTH - buttonWidth - padding,
//                 startY - i * (buttonHeight + padding),
//                 buttonWidth,
//                 buttonHeight
//             );
//         }

//         // Scroll wheel handler
//         Gdx.input.setInputProcessor(new InputAdapter() {
//             @Override
//             public boolean scrolled(float amountX, float amountY) {
//                 if (amountY > 0) {
//                     particleIndex = (particleIndex + 1) % particleTypes.length;
//                 } else if (amountY < 0) {
//                     particleIndex = (particleIndex - 1 + particleTypes.length) % particleTypes.length;
//                 }
//                 currentParticle = particleTypes[particleIndex];
//                 log.info("Selected particle: " + currentParticle);
//                 return true;
//             }
//         });
//     }

//     private Texture safeLoad(String fileName) {
//         try {
//             return new Texture(Gdx.files.internal(fileName));
//         } catch (Exception e) {
//             log.log(Level.WARNING, "Missing texture: {0}", fileName);
//             return null;
//         }
//     }

//     @Override
//     public void render() {
//         handleInput();

//         float r = 29f / 255f, g = 31f / 255f, b = 33f / 255f;
//         Gdx.gl.glClearColor(r, g, b, 1);
//         Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//         // Parallel update
//         parallelUpdate();

//         batch.setProjectionMatrix(camera.combined);
//         batch.begin();

//         // Render particles
//         for (int x = 0; x < GRID_WIDTH; x++) {
//             for (int y = 0; y < GRID_HEIGHT; y++) {
//                 Particle p = grid[x][y];
//                 if (p == null) continue;
//                 if (p instanceof SandParticle) {
//                     batch.setColor(SANDCOLOR);
//                 } else if (p instanceof WaterParticle) {
//                     batch.setColor(WATERCOLOR);
//                 } else if (p instanceof WetSandParticle) {
//                     batch.setColor(WETSANDCOLOR);
//                 } else if (p instanceof VaporParticle) {
//                     batch.setColor(VAPORCOLOR);
//                 } else if (p instanceof LavaParticle) {
//                     batch.setColor(LAVACOLOR);
//                 } else if (p instanceof StoneParticle) {
//                     batch.setColor(STONECOLOR);
//                 } else if (p instanceof AshParticle) {
//                     batch.setColor(ASHCOLOR);
//                 } else if (p instanceof PowderParticle) {
//                     batch.setColor(POWDERCOLOR);
//                 } else if (p instanceof SmokeParticle) {
//                     batch.setColor(SMOKECOLOR);
//                 } else if (p instanceof SnowParticle) {
//                     batch.setColor(SNOWCOLOR);
//                 } else if (p instanceof CarbonParticle) {
//                     batch.setColor(CARBONCOLOR);
//                 } else if (p instanceof VoidParticle) {
//                     batch.setColor(VOIDCOLOR);
//                 }
//                 batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
//             }
//         }

//         // Render sidebar
//         for (int i = 0; i < particleTypes.length; i++) {
//             ParticleType type = particleTypes[i];
//             Color color = getColorForType(type);
//             drawButton(paletteButtons[i], color, type.name(), type);
//         }

//         batch.end();
//     }

//     private void parallelUpdate() {
//         int chunkHeight = GRID_HEIGHT / THREAD_COUNT;

//         // clear nextGrid
//         for (int x = 0; x < GRID_WIDTH; x++) {
//             for (int y = 0; y < GRID_HEIGHT; y++) {
//                 nextGrid[x][y] = null;
//             }
//         }

//         // submit work
//         for (int t = 0; t < THREAD_COUNT; t++) {
//             final int startY = t * chunkHeight;
//             final int endY = (t == THREAD_COUNT - 1) ? GRID_HEIGHT : startY + chunkHeight;

//             executor.submit(() -> {
//                 for (int y = endY - 1; y >= startY; y--) {
//                     for (int x = 0; x < GRID_WIDTH; x++) {
//                         Particle p = grid[x][y];
//                         if (p != null) {
//                             // update writes into nextGrid
//                             p.update(0.1f, grid, nextGrid);
//                         }
//                     }
//                 }
//             });
//         }

//         try {
//             executor.shutdown();
//             executor.awaitTermination(1, TimeUnit.MINUTES);
//         } catch (InterruptedException e) {
//             log.log(Level.SEVERE, "Threaded update interrupted", e);
//         }

//         // swap grids
//         Particle[][] temp = grid;
//         grid = nextGrid;
//         nextGrid = temp;
//     }

//     private Color getColorForType(ParticleType type) {
//         return switch (type) {
//             case SAND -> SANDCOLOR;
//             case WATER -> WATERCOLOR;
//             case WETSAND -> WETSANDCOLOR;
//             case VAPOR -> VAPORCOLOR;
//             case LAVA -> LAVACOLOR;
//             case STONE -> STONECOLOR;
//             case ASH -> ASHCOLOR;
//             case POWDER -> POWDERCOLOR;
//             case SMOKE -> SMOKECOLOR;
//             case SNOW -> SNOWCOLOR;
//             case CARBON -> CARBONCOLOR;
//             case VOID -> VOIDCOLOR;
//         };
//     }

//     private void drawButton(Rectangle rect, Color color, String label, ParticleType type) {
//         batch.setColor(color);
//         batch.draw(pixel, rect.x, rect.y, rect.width, rect.height);

//         if (currentParticle == type) {
//             batch.setColor(Color.WHITE);
//             batch.draw(pixel, rect.x - 2, rect.y + rect.height, rect.width + 4, 2);
//             batch.draw(pixel, rect.x - 2, rect.y - 2, rect.width + 4, 2);
//             batch.draw(pixel, rect.x - 2, rect.y - 2, 2, rect.height + 4);
//             batch.draw(pixel, rect.x + rect.width, rect.y - 2, 2, rect.height + 4);
//         }

//         font.setColor(Color.WHITE);
//         font.draw(batch, label, rect.x + 5, rect.y + rect.height / 1.5f);
//     }

//     private void handleInput() {
//         if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
//             currentStyle = (currentStyle == RenderStyle.PIXEL) ? RenderStyle.TEXTURE : RenderStyle.PIXEL;
//             log.info("Switched render style to " + currentStyle);
//         }

//         if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
//             worldCoordinates.set(Gdx.input.getX(), Gdx.input.getY(), 0);
//             viewport.unproject(worldCoordinates);

//             int mouseX = (int)(worldCoordinates.x / CELL_SIZE);
//             int mouseY = (int)(worldCoordinates.y / CELL_SIZE);

//             int brushRadius = 9;
//             for (int dx = -brushRadius; dx <= brushRadius; dx++) {
//                 for (int dy = -brushRadius; dy <= brushRadius; dy++) {
//                     if (dx * dx + dy * dy <= brushRadius * brushRadius) {
//                         int x = mouseX + dx;
//                         int y = mouseY + dy;

//                         if (x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT && grid[x][y] == null) {
//                             switch (currentParticle) {
//                                 case SAND -> grid[x][y] = new SandParticle(x, y, "sand");
//                                 case WATER -> grid[x][y] = new WaterParticle(x, y, "water");
//                                 case WETSAND -> grid[x][y] = new WetSandParticle(x, y, "wetsand");
//                                 case VAPOR -> grid[x][y] = new VaporParticle(x, y, "vapor");
//                                 case LAVA -> grid[x][y] = new LavaParticle(x, y, "lava");
//                                 case STONE -> grid[x][y] = new StoneParticle(x, y, "stone");
//                                 case ASH -> grid[x][y] = new AshParticle(x, y, "ash");
//                                 case POWDER -> grid[x][y] = new PowderParticle(x, y, "powder");
//                                 case SMOKE -> grid[x][y] = new SmokeParticle(x, y, "smoke");
//                                 case SNOW -> grid[x][y] = new SnowParticle(x, y, "snow");
//                                 case CARBON -> grid[x][y] = new CarbonParticle(x, y, "carbon");
//                                 case VOID -> grid[x][y] = new VoidParticle(x, y, "void");
//                             }
//                         }
//                     }
//                 }
//             }
//         }
//     }

//     @Override
//     public void dispose() {
//         batch.dispose();
//         pixel.dispose();
//         font.dispose();
//         if (sandTex != null) sandTex.dispose();
//         if (waterTex != null) waterTex.dispose();
//         if (lavaTex != null) lavaTex.dispose();
//         if (stoneTex != null) stoneTex.dispose();
//         if (snowTex != null) snowTex.dispose();
//         executor.shutdownNow();
//     }

//     @Override
//     public void resize(int width, int height) {
//         viewport.update(width, height);
//         camera.position.set(GRID_WIDTH / 2f, GRID_HEIGHT / 2f, 0);
//         camera.update();
//     }
// }
