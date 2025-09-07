package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;
;

public class WetSandParticle extends Particle {
    private int sinkCounter = 0;
    private static final float SINK_DELAY = 0.75f;

    public WetSandParticle(int x, int y, String id) {
        super(x, y, id);
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
}
