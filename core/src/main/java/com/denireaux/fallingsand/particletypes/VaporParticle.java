package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;

public class VaporParticle extends Particle {

    public VaporParticle(int x, int y) {
        super(x, y);
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 8;
        // Invert gravity: vapor rises
        velocity -= gravity;

        // Cap velocity so it doesn't fly too fast
        float maxVelocity = 1.5f;
        velocity = Math.max(velocity, -maxVelocity);

        int moveSteps = (int) Math.abs(velocity);
        if (moveSteps == 0) return;

        for (int i = 0; i < moveSteps; i++) {
            if (y >= grid[0].length - 1) {
                velocity = 0;
                return; // Hit top of screen
            }

            // Try to float up
            if (MovementHelper.canMoveUp(grid, x, y)) {
                moveUp(grid);
                continue;
            }

            // Try to slide sideways near ceiling
            boolean canLeft = MovementHelper.canLeft(grid, x, y);
            boolean canRight = MovementHelper.canRight(grid, x, y);
            boolean moveLeft = utils.getRandomBoolean();

            if (canLeft && canRight) {
                if (moveLeft) {
                    moveLeft(grid);
                } else {
                    moveRight(grid);
                }
                continue;
            } else if (canLeft) {
                moveLeft(grid);
                continue;
            } else if (canRight) {
                moveRight(grid);
                continue;
            } else {
                velocity = 0;
                break;
            }
        }

        velocity += moveSteps; // smooth deceleration
    }

    private void moveUp(Particle[][] grid) {
        grid[x][y] = null;
        grid[x][y + 1] = this;
        y++;
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

    @Override
    public void moveDown(Particle[][] grid) {}
}
