package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.particletypes.Particle;
import com.denireaux.fallingsand.helpers.MovementHelper;

public class WetSandParticle extends Particle {
    private int sinkCounter = 0;
    private static final float SINK_DELAY = 0.75f;

    public WetSandParticle(int x, int y) {
        super(x, y);
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 8;
        velocity += gravity;
        if (velocity > 1.0f) velocity = 1.0f;

        int moveSteps = (int) velocity;
        for (int i = 0; i < moveSteps; i++) {
            if (y <= 0) return;

            if (MovementHelper.canMoveDown(grid, x, y)) {
                moveDown(grid);
                continue;
            } else if (grid[x][y - 1] instanceof WaterParticle) {
                if (sinkCounter >= SINK_DELAY) {
                    swapWith(grid, x, y - 1);
                    sinkCounter = 0;
                    continue;
                } else {
                    sinkCounter++;
                }
            }
        }

        velocity -= moveSteps;
    }

    @Override 
    public void moveDown(Particle[][] grid) {
        grid[x][y] = null;
        grid[x][y - 1] = this;
        y--;
    }

    @Override 
    public void moveRight(Particle[][] grid) {
        // Optional: implement later
    }

    @Override 
    public void moveLeft(Particle[][] grid) {
        // Optional: implement later
    }

    private void swapWith(Particle[][] grid, int newX, int newY) {
        Particle temp = grid[newX][newY];
        grid[newX][newY] = this;
        grid[x][y] = temp;
        if (temp != null) {
            temp.x = x;
            temp.y = y;
        }
        x = newX;
        y = newY;
    }
}
