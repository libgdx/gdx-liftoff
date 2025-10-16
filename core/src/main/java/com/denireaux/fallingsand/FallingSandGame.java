package com.denireaux.fallingsand;

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
import com.denireaux.fallingsand.particletypes.OilParticle;
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

    public static final int GRID_WIDTH = 1500;
    public static final int GRID_HEIGHT = 1000;
    public static final int CELL_SIZE = 1;

    private SpriteBatch batch;
    private Texture pixel;
    private BitmapFont font;
    private static Particle[][] grid;

    private enum RenderStyle { PIXEL }
    private RenderStyle currentStyle = RenderStyle.PIXEL;

    private enum ParticleType {
        SAND, WATER, WETSAND, VAPOR, LAVA,
        STONE, ASH, POWDER, SMOKE, SNOW,
        CARBON, VOID, OIL
    }

    private ParticleType currentParticle = ParticleType.SAND;
    private int particleIndex = 0;
    private final ParticleType[] particleTypes = ParticleType.values();

    private boolean glowEnabled = true;
    private Rectangle[] paletteButtons;

    // Neon particle colors (optimized for black background)
    private final Color SANDCOLOR     = new Color(1.00f, 0.96f, 0.47f, 1f);
    private final Color WATERCOLOR    = new Color(0.00f, 0.90f, 1.00f, 1f);
    private final Color WETSANDCOLOR  = new Color(1.00f, 0.64f, 0.00f, 1f);
    private final Color VAPORCOLOR    = new Color(0.80f, 0.85f, 0.90f, 1f);
    private final Color LAVACOLOR     = new Color(1.00f, 0.00f, 0.00f, 1f);
    private final Color STONECOLOR    = new Color(0.60f, 0.60f, 0.60f, 1f);
    private final Color ASHCOLOR      = new Color(0.80f, 0.80f, 0.80f, 1f);
    private final Color POWDERCOLOR   = new Color(0.87f, 0.84f, 0.78f, 1f);
    private final Color SMOKECOLOR    = new Color(0.40f, 0.40f, 0.40f, 1f);
    private final Color SNOWCOLOR     = new Color(1.00f, 1.00f, 1.00f, 1f);
    private final Color CARBONCOLOR   = new Color(0.20f, 0.20f, 0.20f, 1f);
    private final Color VOIDCOLOR     = new Color(0.00f, 1.00f, 0.50f, 1f);
    private final Color OILCOLOR      = new Color(0.20f, 0.12f, 0.05f, 1f);

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

        // Sidebar UI setup
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

        // Scroll wheel switching
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                if (amountY > 0)
                    particleIndex = (particleIndex + 1) % particleTypes.length;
                else if (amountY < 0)
                    particleIndex = (particleIndex - 1 + particleTypes.length) % particleTypes.length;
                currentParticle = particleTypes[particleIndex];
                return true;
            }
        });
    }

    @Override
    public void render() {
        handleInput();

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for (int y = GRID_HEIGHT - 1; y >= 0; y--) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (grid[x][y] != null)
                    grid[x][y].update(0.1f, grid);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            glowEnabled = !glowEnabled;
            System.out.println("Glow mode: " + (glowEnabled ? "ON" : "OFF"));
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                Particle p = grid[x][y];
                if (p == null) continue;

                Color baseColor = getColorForType(p);
                if (baseColor == null) continue;

                boolean shouldGlow = glowEnabled && (
                        p instanceof LavaParticle ||
                        p instanceof VaporParticle ||
                        p instanceof SmokeParticle
                );

                if (shouldGlow) {
                    batch.setColor(baseColor.r, baseColor.g, baseColor.b, 0.3f);
                    batch.draw(pixel, x * CELL_SIZE - 1, y * CELL_SIZE - 1, CELL_SIZE + 2, CELL_SIZE + 2);
                }

                batch.setColor(baseColor);
                batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        // Sidebar
        for (int i = 0; i < particleTypes.length; i++) {
            Color color = getColorForType(particleTypes[i]);
            drawButton(paletteButtons[i], color, particleTypes[i].name(), particleTypes[i]);
        }

        batch.end();
    }

    private Color getColorForType(Object type) {
        if (type instanceof SandParticle || type == ParticleType.SAND) return SANDCOLOR;
        if (type instanceof WaterParticle || type == ParticleType.WATER) return WATERCOLOR;
        if (type instanceof WetSandParticle || type == ParticleType.WETSAND) return WETSANDCOLOR;
        if (type instanceof VaporParticle || type == ParticleType.VAPOR) return VAPORCOLOR;
        if (type instanceof LavaParticle || type == ParticleType.LAVA) return LAVACOLOR;
        if (type instanceof StoneParticle || type == ParticleType.STONE) return STONECOLOR;
        if (type instanceof AshParticle || type == ParticleType.ASH) return ASHCOLOR;
        if (type instanceof PowderParticle || type == ParticleType.POWDER) return POWDERCOLOR;
        if (type instanceof SmokeParticle || type == ParticleType.SMOKE) return SMOKECOLOR;
        if (type instanceof SnowParticle || type == ParticleType.SNOW) return SNOWCOLOR;
        if (type instanceof CarbonParticle || type == ParticleType.CARBON) return CARBONCOLOR;
        if (type instanceof VoidParticle || type == ParticleType.VOID) return VOIDCOLOR;
        if (type instanceof OilParticle || type == ParticleType.OIL) return OILCOLOR;
        return null;
    }

    private void drawButton(Rectangle rect, Color color, String label, ParticleType type) {
        batch.setColor(color);
        batch.draw(pixel, rect.x, rect.y, rect.width, rect.height);
        if (currentParticle == type) {
            batch.setColor(Color.WHITE);
            batch.draw(pixel, rect.x - 2, rect.y - 2, rect.width + 4, rect.height + 4);
        }
        font.setColor(Color.WHITE);
        font.draw(batch, label, rect.x + 5, rect.y + rect.height / 1.5f);
    }

    private void handleInput() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            worldCoordinates.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(worldCoordinates);

            int mouseX = (int) (worldCoordinates.x / CELL_SIZE);
            int mouseY = (int) (worldCoordinates.y / CELL_SIZE);

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
                                case OIL -> grid[x][y] = new OilParticle(x, y, "oil");
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
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(GRID_WIDTH / 2f, GRID_HEIGHT / 2f, 0);
        camera.update();
    }
}
