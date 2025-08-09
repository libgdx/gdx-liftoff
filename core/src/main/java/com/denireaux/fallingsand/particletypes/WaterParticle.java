package com.denireaux.fallingsand.particletypes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;

public class WaterParticle extends Particle {

    public WaterParticle(int x, int y) {
        super(x, y);
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 8;
        velocity += gravity;

        // Cap velocity to prevent excessive speed
        float maxVelocity = 1.3f;
        velocity = Math.min(velocity, maxVelocity);

        int moveSteps = (int) velocity;
        if (moveSteps == 0) return;

        for (int i = 0; i < moveSteps; i++) {
            if (y <= 0) return;

            if (grid[x][y - 1] instanceof VaporParticle) swapWith(grid, x, y);

            // Try to move straight down
            if (MovementHelper.canMoveDown(grid, x, y)) {
                moveDown(grid);
                continue;
            }

            // Try diagonals
            boolean left = MovementHelper.canMoveDownLeft(grid, x, y);
            boolean right = MovementHelper.canMoveDownRight(grid, x, y);

            boolean movingLeft = utils.getRandomBoolean();

            if (left && right) {
                if (movingLeft) {
                    moveLeft(grid);
                } else {
                    moveRight(grid);
                }
                continue;
            } else if (left) {
                moveLeft(grid);
                continue;
            } else if (right) {
                moveRight(grid);
                continue;
            }

            // Slide left or right if completely blocked
            boolean slid = trySlideHorizontally(grid);
            if (!slid) velocity = 0; // Stop if blocked
        }

        velocity -= moveSteps;
    }

    private boolean trySlideHorizontally(Particle[][] grid) {
        List<Integer> offsets = Arrays.asList(1, 2, -1, -2);
        Collections.shuffle(offsets);

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

    @Override
    public void moveDown(Particle[][] grid) {
        grid[x][y] = null;
        grid[x][y - 1] = this;
        y--;
    }

    /**
     * Moves the particle one cell to the left.
     *
     * @param grid the 2D particle array
     */
    public void moveLeft(Particle[][] grid) {
        grid[x][y] = null;
        grid[x - 1][y] = this;
        x--;
    }

    /**
     * Moves the particle one cell to the right.
     *
     * @param grid the 2D particle array
     */
    public void moveRight(Particle[][] grid) {
        grid[x][y] = null;
        grid[x + 1][y] = this;
        x++;
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
