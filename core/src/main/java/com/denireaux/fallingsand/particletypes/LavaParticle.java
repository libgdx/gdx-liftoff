package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;

public class LavaParticle extends Particle {
    public static boolean isHot = true;
    public LavaParticle(int x, int y, String id) {
        super(x, y, id);
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
            if (MovementHelper.canMoveDown(grid, x, y)) {
                moveDown(grid);
                return;
            }

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

}
