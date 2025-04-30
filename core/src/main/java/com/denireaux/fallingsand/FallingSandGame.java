package com.denireaux.fallingsand;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.denireaux.fallingsand.particletypes.Particle;
import com.denireaux.fallingsand.particletypes.SandParticle;

public class FallingSandGame extends ApplicationAdapter {
    public static final int GRID_WIDTH = 400;
    public static final int GRID_HEIGHT = 300;
    public static final int CELL_SIZE = 2;

    private SpriteBatch batch;
    private Texture pixel;
    private Particle[][] grid;

    @Override
    public void create() {
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
                    grid[x][y].update(0.8f, grid);
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
                }
            }
        }
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            int mouseX = Gdx.input.getX() / CELL_SIZE;
            int mouseY = (Gdx.graphics.getHeight() - Gdx.input.getY()) / CELL_SIZE;

            if (mouseX >= 0 && mouseX < GRID_WIDTH && mouseY >= 0 && mouseY < GRID_HEIGHT) {
                grid[mouseX][mouseY] = new SandParticle(mouseX, mouseY);
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        pixel.dispose();
    }
}
