package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.particletypes.Particle;
import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.*;;

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
                    sinkCounter = 0;
                    continue;
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
