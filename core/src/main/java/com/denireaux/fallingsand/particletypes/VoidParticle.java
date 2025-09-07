package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;

public class VoidParticle extends Particle {
    
    public VoidParticle(int x, int y, String id) {
        super(x, y, id);
    }

    private int sinkCounter = 0;
    private static final float SINK_DELAY = 0.75f;
    protected final String id = "void";

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
            if (y <= 0) break;
            if (x >= grid.length) break;
    
            // if (MovementHelper.canMoveDown(grid, x, y)) {
            //     moveDown(grid);
            //     continue;
            // } else if (grid[x][y - 1] instanceof WaterParticle) {
            //     if (sinkCounter >= SINK_DELAY) {
            //         swapWith(grid, x, y - 1);
            //         grid[x][y] = new WetSandParticle(x, y);
            //         sinkCounter = 0;
            //         return;
            //     } else {
            //         sinkCounter++;
            //     }
            // }

            // boolean canDownLeft = MovementHelper.canMoveDownLeft(grid, x, y);
            // boolean canDownRight = MovementHelper.canMoveDownRight(grid, x, y);
            

            
            // boolean movingDownLeft = utils.getRandomBoolean();
    
            // if (canDownLeft && canDownRight) {
            //     if (movingDownLeft) {
            //         moveDownLeft(grid);
            //         continue;
            //     } else {
            //         moveDownRight(grid);
            //         continue;
            //     }
            // } else if (canDownRight) {
            //     moveDownRight(grid);
            //     continue;
            // } else if (canDownLeft) {
            //     moveDownLeft(grid);
            //     continue;
            // } else {
            //     velocity = 0;
            //     break;
            // }


            boolean canDown = MovementHelper.canMoveDown(grid, x, y);
            boolean canLeft = MovementHelper.canLeft(grid, x, y);
            boolean canRight = MovementHelper.canRight(grid, x, y);
            boolean canDownLeft = MovementHelper.canMoveDownLeft(grid, x, y);
            boolean canDownRight = MovementHelper.canMoveDownRight(grid, x, y);
            boolean leftFactor = utils.getRandomBoolean();

            Particle particleBelow = getSurroundingParticles(grid)[0];

            // if ("water".equals(particleBelow.id)) {
            //     swapWith(grid, x, y + 1);
            //     break;
            // }

            try {
                if ("water".equals(particleBelow.id)) swapWith(grid, x, y);
                swapWith(grid, x, y + 5);
            } catch (Exception e) {

            }

            if (canDown) {
                moveDown(grid);
                break;
            }

            if (canLeft && canRight) {
                if (leftFactor) {
                    moveLeft(grid);
                    break;
                }
                moveRight(grid);
            }

            if (canDownLeft && !canDownRight) {
                moveDownLeft(grid);
                break;
            }

            if (canDownRight && !canDownLeft) {
                moveDownRight(grid);
                break;
            }

            if (canDownLeft && canDownRight) {
                if (leftFactor) {
                    moveDownLeft(grid);
                    break;
                }
                moveDownRight(grid);
            }
        }
    
        velocity -= moveSteps;
    }
}
