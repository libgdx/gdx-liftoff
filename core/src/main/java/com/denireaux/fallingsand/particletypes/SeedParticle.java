package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.behaviors.ISolid;
import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;

public class SeedParticle extends Particle implements ISolid {

    private boolean isNearWetSoil;

    public SeedParticle(int x, int y, String id) {
        super(x, y, id);
        this.id = "seed";
        this.isNearWetSoil = false;
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 9;
        velocity += gravity;
        float maxVelocity = 1.5f;
        velocity = Math.min(velocity, maxVelocity);
        int moveSteps = (int) velocity;
        if (moveSteps == 0) return;

        for (int i = 0; i < moveSteps; i++) {
            checkInbounds(grid, x, y);
            checkForGrowAsGrass(grid, x, y);
            moveAsSolid(grid, x, y);
        }
        velocity -= moveSteps;
    }

    @Override
    public void tryNormalMovement(Particle[][] grid) {
        boolean canMoveDown = MovementHelper.canMoveDown(grid, x, y);
        boolean canLeft = MovementHelper.canLeft(grid, x, y);
        boolean canRight = MovementHelper.canRight(grid, x, y);
        boolean leftFactor = utils.getRandomBoolean();
        if (canMoveDown) {
            moveDown(grid);
        }
        if (canLeft && canRight) {
            if (leftFactor) {
                moveLeft(grid);
                return;
            }
            moveRight(grid);
        }
    }

    @Override
    public void moveAsSolid(Particle[][] grid, int x, int y) {
        checkInbounds(grid, x, y);
        tryNormalMovement(grid);
    }

    private void checkForGrowAsGrass(Particle[][] grid, int x, int y) {
        Particle[] surroundingParticles = getSurroundingParticles(grid);

        for (Particle particle : surroundingParticles) {
            if (particle == null) continue;

            if ("wetsoil".equals(particle.getId())) {
                grid[x][y] = null;
                grid[x][y] = new GrassParticle(x, y, id);
            }
        }
    }
}
