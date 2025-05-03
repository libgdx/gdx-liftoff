package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;

public class SandParticle extends Particle {
    public SandParticle(int x, int y) {
        super(x, y);
    }

    // @Override
    // public void update(float gravity, Particle[][] grid) {
    //     velocity += gravity;

    //     if (y <= 0) return;

    //     Particle[] particles = getSurroundingParticles(grid);
    //     Particle particleLeft = particles[0];
    //     Particle particleRight = particles[1];
    //     Particle particleBelow = particles[3];

    //     // Down
    //     if (y > 0 && grid[x][y - 1] == null) {
    //         moveDown(grid, x, y - 1);
    //         return;
    //     }

    //     // Down-right
    //     if (x + 1 < grid.length && y - 1 >= 0 && particleRight == null && grid[x + 1][y - 1] == null) {
    //         moveDownRight(grid);
    //         return;
    //     }

    //     // Down-left
    //     if (x - 1 >= 0 && y - 1 >= 0 && particleLeft == null && grid[x - 1][y - 1] == null) {
    //         moveDownLeft(grid);
    //         return;
    //     }
    // }

    // TODO: Fix invisible sand
    // For whatever reason the sand when spawned, is completely invisible
    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 6;
        velocity += gravity;
    
        // Prevent sand from falling too fast
        float maxVelocity = 1.0f;
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
                moveDown(grid, x, y - 1);
                continue;
            }
    
            boolean canDownLeft = MovementHelper.canMoveDownLeft(grid, x, y);
            boolean canDownRight = MovementHelper.canMoveDownRight(grid, x, y);
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
    

    private void moveDown(Particle[][] grid, int newX, int newY) {
        grid[newX][newY] = this;
        grid[x][y] = null;
        x = newX;
        y = newY;
    }

    private void moveDownRight(Particle[][] grid) {
        grid[x][y] = null;
        grid[x + 1][y - 1] = this;
        x++;
        y--;
    }

    private void moveDownLeft(Particle[][] grid) {
        grid[x][y] = null;
        grid[x - 1][y - 1] = this;
        x--;
        y--;
    }

    private void moveLeft(Particle[][] grid) {
        grid[x][y] = null;
        grid[x - 1][y] = this;
        x--;
    }

    private void moveRight(Particle[][] grid) {
        grid[x][y] = null;
        grid[x + 1][y] = this;
        x++;
    }
}
