package com.denireaux.fallingsand;

import java.util.logging.Logger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.denireaux.fallingsand.particletypes.LavaParticle;
import com.denireaux.fallingsand.particletypes.Particle;
import com.denireaux.fallingsand.particletypes.SandParticle;
import com.denireaux.fallingsand.particletypes.StoneParticle;
import com.denireaux.fallingsand.particletypes.VaporParticle;
import com.denireaux.fallingsand.particletypes.VoidParticle;
import com.denireaux.fallingsand.particletypes.WaterParticle;
import com.denireaux.fallingsand.particletypes.WetSandParticle;

public class FallingSandGame extends ApplicationAdapter {
    private static final Logger log = Logger.getLogger(String.valueOf(FallingSandGame.class));

    public static final int GRID_WIDTH = 1200;
    public static final int GRID_HEIGHT = 800;
    public static final int CELL_SIZE = 1;

    private SpriteBatch batch;
    private Texture pixel;
    private BitmapFont font;
    private static Particle[][] grid;

    private enum ParticleType {
        SAND,
        WATER,
        WETSAND,
        VAPOR,
        LAVA,
        STONE,
        VOID
    }

    private ParticleType currentParticle = ParticleType.SAND;

    // Palette buttons
    private Rectangle sandButton, waterButton, wetSandButton, vaporButton, lavaButton, stoneButton, voidButton;

    // Button colors
    private final Color SANDCOLOR = Color.YELLOW;
    private final Color WATERCOLOR = Color.BLUE;
    private final Color WETSANDCOLOR = Color.BROWN;
    private final Color VAPORCOLOR = Color.LIGHT_GRAY;
    private final Color LAVACOLOR = Color.RED;
    private final Color STONECOLOR = Color.DARK_GRAY;
    private final Color VOIDCOLOR = Color.WHITE;

    @Override
    public void create() {
        Gdx.graphics.setForegroundFPS(120);
        batch = new SpriteBatch();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();

        font = new BitmapFont(); // Default font
        grid = new Particle[GRID_WIDTH][GRID_HEIGHT];

        int buttonWidth = 80;
        int buttonHeight = 40;
        int y = 10; // padding from bottom

        sandButton = new Rectangle(10, y, buttonWidth, buttonHeight);
        waterButton = new Rectangle(100, y, buttonWidth, buttonHeight);
        wetSandButton = new Rectangle(190, y, buttonWidth, buttonHeight);
        vaporButton = new Rectangle(280, y, buttonWidth, buttonHeight);
        lavaButton = new Rectangle(370, y, buttonWidth, buttonHeight);
        stoneButton = new Rectangle(460, y, buttonWidth, buttonHeight);
        voidButton = new Rectangle(550, y, buttonWidth, buttonHeight);
    }

    @Override
    public void render() {
        handleInput();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update particles
        for (int y = GRID_HEIGHT - 1; y >= 0; y--) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (grid[x][y] != null) {
                    grid[x][y].update(0.1f, grid);
                }
            }
        }

        // Draw particles
        batch.begin();
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (grid[x][y] instanceof SandParticle) {
                    batch.setColor(Color.YELLOW);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof WaterParticle) {
                    batch.setColor(Color.BLUE);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof WetSandParticle) {
                    batch.setColor(Color.BROWN);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof VaporParticle) {
                    batch.setColor(Color.LIGHT_GRAY);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof LavaParticle) {
                    batch.setColor(Color.RED);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof StoneParticle) {
                    batch.setColor(Color.DARK_GRAY);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (grid[x][y] instanceof VoidParticle) {
                    batch.setColor(Color.WHITE);
                    batch.draw(pixel, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        // Draw palette buttons
        drawButton(sandButton, SANDCOLOR, "Sand", ParticleType.SAND);
        drawButton(waterButton, WATERCOLOR, "Water", ParticleType.WATER);
        drawButton(wetSandButton, WETSANDCOLOR, "Wet Sand", ParticleType.WETSAND);
        drawButton(vaporButton, VAPORCOLOR, "Vapor", ParticleType.VAPOR);
        drawButton(lavaButton, LAVACOLOR, "Lava", ParticleType.LAVA);
        drawButton(stoneButton, STONECOLOR, "Stone", ParticleType.STONE);
        drawButton(voidButton, VOIDCOLOR, "VOID", ParticleType.VOID);

        batch.end();
    }

    private void drawButton(Rectangle rect, Color color, String label, ParticleType type) {
        // Button background
        batch.setColor(color);
        batch.draw(pixel, rect.x, rect.y, rect.width, rect.height);

        // Highlight border if selected
        if (currentParticle == type) {
            batch.setColor(Color.WHITE);
            // Top
            batch.draw(pixel, rect.x - 2, rect.y + rect.height, rect.width + 4, 2);
            // Bottom
            batch.draw(pixel, rect.x - 2, rect.y - 2, rect.width + 4, 2);
            // Left
            batch.draw(pixel, rect.x - 2, rect.y - 2, 2, rect.height + 4);
            // Right
            batch.draw(pixel, rect.x + rect.width, rect.y - 2, 2, rect.height + 4);
        }

        // Label
        font.setColor(Color.WHITE);
        font.draw(batch, label, rect.x, rect.y + rect.height + 15);
    }

    private void handleInput() {
        // Check button clicks
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (sandButton.contains(mouseX, mouseY)) {
                currentParticle = ParticleType.SAND;
                log.info("Switched to Sand");
                return;
            } else if (waterButton.contains(mouseX, mouseY)) {
                currentParticle = ParticleType.WATER;
                log.info("Switched to Water");
                return;
            } else if (wetSandButton.contains(mouseX, mouseY)) {
                currentParticle = ParticleType.WETSAND;
                log.info("Switched to Wet Sand");
                return;
            } else if (vaporButton.contains(mouseX, mouseY)) {
                currentParticle = ParticleType.VAPOR;
                log.info("Switched to Vapor");
                return;
            } else if (lavaButton.contains(mouseX, mouseY)) {
                currentParticle = ParticleType.LAVA;
                log.info("Switched to Lava");
                return;
            } else if (stoneButton.contains(mouseX, mouseY)) {
                currentParticle = ParticleType.STONE;
                log.info("Switched to Stone");
                return;
            } else if (voidButton.contains(mouseX, mouseY)) {
                currentParticle = ParticleType.VOID;
                log.info("Switched to void");
            }
        }

        // Spawn selected particle type
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            int mouseX = Gdx.input.getX() / CELL_SIZE;
            int mouseY = (Gdx.graphics.getHeight() - Gdx.input.getY()) / CELL_SIZE;

            int brushRadius = 8;

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
    }
}
