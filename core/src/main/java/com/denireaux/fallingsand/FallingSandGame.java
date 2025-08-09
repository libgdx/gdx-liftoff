package com.denireaux.fallingsand;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.denireaux.fallingsand.particletypes.LavaParticle;
import com.denireaux.fallingsand.particletypes.Particle;
import com.denireaux.fallingsand.particletypes.SandParticle;
import com.denireaux.fallingsand.particletypes.StoneParticle;
import com.denireaux.fallingsand.particletypes.VaporParticle;
import com.denireaux.fallingsand.particletypes.WaterParticle;
import com.denireaux.fallingsand.particletypes.WetSandParticle;

public class FallingSandGame extends ApplicationAdapter {
    public static final int GRID_WIDTH = 1200;
    public static final int GRID_HEIGHT = 800;
    public static final int CELL_SIZE = 1;

    private SpriteBatch batch;
    private Texture pixel;
    private Particle[][] grid;

    private enum ParticleType {
        SAND,
        WATER,
        WETSAND,
        VAPOR,
        LAVA,
        STONE
    }

    private ParticleType currentParticle = ParticleType.SAND;

    @Override
    public void create() {
        Gdx.graphics.setForegroundFPS(90);
        batch = new SpriteBatch();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();

        grid = new Particle[GRID_WIDTH][GRID_HEIGHT];
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
                }
            }
        }
        batch.end();
    }

    private void handleInput() {
        // Particle type selection
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            currentParticle = ParticleType.SAND;
            System.out.println("Switched to Sand");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            currentParticle = ParticleType.WATER;
            System.out.println("Switched to Water");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            currentParticle = ParticleType.WETSAND;
            System.out.println("Switched to Wet Sand");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            currentParticle = ParticleType.VAPOR;
            System.out.println("Switched to Vapor");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            currentParticle = ParticleType.LAVA;
            System.out.println("Switched to Lava");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            currentParticle = ParticleType.STONE;
            System.out.println("Switched to Stone");
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
                                case SAND:
                                    grid[x][y] = new SandParticle(x, y);
                                    break;
                                case WATER:
                                    grid[x][y] = new WaterParticle(x, y);
                                    break;
                                case WETSAND:
                                    grid[x][y] = new WetSandParticle(x, y);
                                    break;
                                case VAPOR:
                                    grid[x][y] = new VaporParticle(x, y);
                                    break;
                                case LAVA:
                                    grid[x][y] = new LavaParticle(x, y);
                                    break;
                                case STONE:
                                    grid[x][y] = new StoneParticle(x, y);
                                    break;
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
    }
}
