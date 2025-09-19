package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.behaviors.IGas;
import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;

public class SmokeParticle extends Particle implements IGas{

    public static boolean hasCombusted = false;

    public SmokeParticle(int x, int y, String id) {
        super(x, y, id);
        this.isHot = false;
        this.id = "smoke";
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 8;
        velocity -= gravity;
        float maxVelocity = 1.5f;
        velocity = Math.max(velocity, -maxVelocity);

        int moveSteps = (int) Math.abs(velocity);
        if (moveSteps == 0) return;

        for (int i = 0; i < moveSteps; i++) {
            checkInbounds(grid, x, y);
            tryNormalMovementUpwards(grid);
        }
        velocity += moveSteps;
    }

    private void tryNormalMovementUpwards(Particle[][] grid) {
        boolean canMoveUp = MovementHelper.canMoveUp(grid, x, y);
        boolean canLeft = MovementHelper.canLeft(grid, x, y);
        boolean canRight = MovementHelper.canRight(grid, x, y);
        boolean leftFactor = utils.getRandomBoolean();
        Particle[] surroundingParticles = getSurroundingParticles(grid);
        if (surroundingParticles[2] != null && !"stone".equals(surroundingParticles[2].getId())) {
            if ("smoke".equals(surroundingParticles[2].getId())) return;
            swapWith(grid, x, y + 1);
        }
        if (canMoveUp) {
            moveUp(grid);
            tryContinueFloating(grid, x, y);
            return;
        }
        if (canLeft && canRight) {
            if (leftFactor) {
                moveLeft(grid);
                // tryContinueFloating(grid, x, y);
                return;
            }
            moveRight(grid);
            // tryContinueFloating(grid, x, y);
        }
    }

    private void tryContinueFloating(Particle[][] grid, int x, int y) {
        Particle particleAbove = getAboveParticle(grid, x, y);
        if (particleAbove == null) return;
        swapWith(grid, x, y + 1);
    }

    @Override
    public void moveAsGas(Particle[][] grid, int x, int y) {
        checkInbounds(grid, x, y);
        tryNormalMovementUpwards(grid);
    }
}
