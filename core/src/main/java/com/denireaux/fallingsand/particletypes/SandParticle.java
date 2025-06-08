package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;

/**
 * Represents a sand particle in the falling sand simulation.
 * 
 * Sand particles are affected by gravity and fall straight down if possible.
 * When blocked, they attempt to move diagonally left or right.
 * 
 * This class handles basic gravity application and movement logic.
 * 
 * @@author D'Angelo L. DeNiro
 */
public class SandParticle extends Particle {
    public SandParticle(int x, int y) {
        super(x, y);
    }

    private int sinkCounter = 0;
    private static final float SINK_DELAY = 0.75f; // Adjust this to control sink speed


    /**
     * Updates the sand particle's position based on gravity and surrounding particles.
     * This simulates falling behavior and diagonal sliding if the path directly below is blocked.
     *
     * @param gravity the amount of gravity to apply to the particle's vertical velocity
     * @param grid the 2D array representing the current particle grid
     */
    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 8;
        velocity += gravity;
    
        // Prevent sand from falling too fast
        float maxVelocity = 1.3f;
        if (velocity > maxVelocity) {
            velocity = maxVelocity;
        }
    
        // Only move when velocity accumulates at least 1 full tile
        float dy = velocity;
        int moveSteps = (int) dy;
    
        if (moveSteps == 0) return;
    
        for (int i = 0; i < moveSteps; i++) {
            if (y <= 0) return;
    
            if (MovementHelper.canMoveDown(grid, x, y)) {
                moveDown(grid);
                continue;
            } else if (grid[x][y - 1] instanceof WaterParticle) {
                if (sinkCounter >= SINK_DELAY) {
                    swapWith(grid, x, y - 1);
                    grid[x][y] = new WetSandParticle(x, y);
                    sinkCounter = 0;
                    return;
                } else {
                    sinkCounter++;
                }
            }

            boolean canDownLeft = MovementHelper.canMoveDownLeft(grid, x, y);
            boolean canDownRight = MovementHelper.canMoveDownRight(grid, x, y);
            boolean canLeft = MovementHelper.canLeft(grid, x, y);
            boolean canRight = MovementHelper.canRight(grid, x, y);
            
            boolean movingDownLeft = utils.getRandomBoolean();
    
            if (canDownLeft && canDownRight) {
                if (movingDownLeft) {
                    moveDownLeft(grid);
                    continue;
                } else {
                    moveDownRight(grid);
                    continue;
                }
            } else if (canDownRight) {
                moveDownRight(grid);
                continue;
            } else if (canDownLeft) {
                moveDownLeft(grid);
                continue;
            } else {
                velocity = 0;
                break;
            }
        }
    
        velocity -= moveSteps;
    }
    
    /**
     * Moves the particle straight down in the grid.
     *
     * @param grid the 2D particle array
     * @param newX the X-coordinate to move to
     * @param newY the Y-coordinate to move to
     */
    @Override
    public void moveDown(Particle[][] grid) {
        grid[x][y] = null;
        grid[x][y - 1] = this;
        y--;
    }

    /**
     * Moves the particle down and to the right in the grid.
     *
     * @param grid the 2D particle array
     */
    private void moveDownRight(Particle[][] grid) {
        grid[x][y] = null;
        grid[x + 1][y - 1] = this;
        x++;
        y--;
    }

    /**
     * Moves the particle down and to the left in the grid.
     *
     * @param grid the 2D particle array
     */
    private void moveDownLeft(Particle[][] grid) {
        grid[x][y] = null;
        grid[x - 1][y - 1] = this;
        x--;
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
