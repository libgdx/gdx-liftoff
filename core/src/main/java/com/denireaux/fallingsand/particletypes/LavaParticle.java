package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;

public class LavaParticle extends Particle {

    private int heatLevel = 100; // cools over time
    private int waterTouchCounter = 0;
    private static final int SOLIDIFY_THRESHOLD = 3;
    public static boolean isHot = true;

    public LavaParticle(int x, int y) {
        super(x, y);
        // this.isHot = true; // You can use this to ignite other particles
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 6; // Lava is heavy
        velocity += gravity;
        velocity = Math.min(velocity, 1.5f);

        int steps = (int) velocity;
        if (steps == 0) return;

        for (int i = 0; i < steps; i++) {
            if (y <= 0) return;

            // Particle below = grid[x][y - 1];

            // Turn water into vapor and sink
            // if (below instanceof WaterParticle) {
            //     grid[x][y - 1] = new VaporParticle(x, y - 1);
            //     moveDown(grid);
            //     waterTouchCounter++;
            //     // if (waterTouchCounter >= SOLIDIFY_THRESHOLD) {
            //     //     grid[x][y] = new StoneParticle(x, y);
            //     // }
            //     return;
            // }

            // Sink if space is available
            if (MovementHelper.canMoveDown(grid, x, y)) {
                moveDown(grid);
                return;
            }

            // Sideways movement (thick liquid)
            boolean canLeft = MovementHelper.canLeft(grid, x, y);
            boolean canRight = MovementHelper.canRight(grid, x, y);
            if (canLeft && canRight) {
                if (utils.getRandomBoolean()) moveLeft(grid);
                else moveRight(grid);
            } else if (canLeft) {
                moveLeft(grid);
            } else if (canRight) {
                moveRight(grid);
            } else {
                velocity = 0;
                break;
            }
        }

        velocity -= steps;
    }

    @Override
    public void moveDown(Particle[][] grid) {
        grid[x][y] = null;
        grid[x][y - 1] = this;
        y--;
    }

    @Override
    public void moveLeft(Particle[][] grid) {
        grid[x][y] = null;
        grid[x - 1][y] = this;
        x--;
    }

    @Override
    public void moveRight(Particle[][] grid) {
        grid[x][y] = null;
        grid[x + 1][y] = this;
        x++;
    }
}
