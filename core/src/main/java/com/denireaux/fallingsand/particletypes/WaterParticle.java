package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;

public class WaterParticle extends Particle {

    public WaterParticle(int x, int y) {
        super(x, y);
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        velocity += gravity;

        // Cap velocity
        float maxVelocity = 1.0f;
        velocity = Math.min(velocity, maxVelocity);

        int moveSteps = (int) velocity;
        if (moveSteps == 0) return;

        for (int i = 0; i < moveSteps; i++) {
            if (y <= 0) return;

            // Try to move straight down
            if (MovementHelper.canMoveDown(grid, x, y)) {
                moveTo(grid, x, y - 1);
                continue;
            }

            // Diagonal down-left or down-right
            boolean left = MovementHelper.canMoveDownLeft(grid, x, y);
            boolean right = MovementHelper.canMoveDownRight(grid, x, y);
            boolean favorLeft = utils.getRandomBoolean();

            if (left && right) {
                moveTo(grid, favorLeft ? x - 1 : x + 1, y - 1);
                continue;
            } else if (left) {
                moveTo(grid, x - 1, y - 1);
                continue;
            } else if (right) {
                moveTo(grid, x + 1, y - 1);
                continue;
            }

            // Slide horizontally if stuck
            boolean slid = trySlideHorizontally(grid);
            if (!slid) velocity = 0; // Stop if blocked
        }

        velocity -= moveSteps;
    }

    private boolean trySlideHorizontally(Particle[][] grid) {
        int[] offsets = utils.getShuffledArray(new int[]{1, 2, -1, -2});
        for (int dx : offsets) {
            int targetX = x + dx;
            if (isInBounds(targetX, y, grid) && grid[targetX][y] == null) {
                moveTo(grid, targetX, y);
                return true;
            }
        }
        return false;
    }

    private void moveTo(Particle[][] grid, int newX, int newY) {
        grid[x][y] = null;
        x = newX;
        y = newY;
        grid[x][y] = this;
    }

    private boolean isInBounds(int x, int y, Particle[][] grid) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;
    }
}
