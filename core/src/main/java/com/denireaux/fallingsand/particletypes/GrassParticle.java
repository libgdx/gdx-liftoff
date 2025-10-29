package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.utils.utils;

public class GrassParticle extends Particle {

    public GrassParticle(int x, int y, String id) {
        super(x, y, id);
        this.id = "plant";
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
            tryNormalMovement(grid);
        }

        velocity -= moveSteps;
    }

    @Override
    public void tryNormalMovement(Particle[][] grid) {
        tryGrow(grid);
    }

    private void tryGrow(Particle[][] grid) {
        Particle[] surroundingParticles = getSurroundingParticles(grid);
        Particle aboveParticle = surroundingParticles[2];
        Particle leftParticle = surroundingParticles[0];
        Particle rightParticle = surroundingParticles[1];

        if (aboveParticle == null) {
            boolean growFactor = utils.getUnfairBoolean(20);
            if (growFactor) grid[x][y - 1] = new GrassParticle(x, y + 1, id);
        }

        boolean spreadFactor = utils.getUnfairBoolean(20);

        if (spreadFactor) {
            if (leftParticle == null) {
                boolean growFactor = utils.getUnfairBoolean(20);
                if (growFactor) grid[x - 1][y] = new GrassParticle(x - 1, y, id);
            }

            if (rightParticle == null) {
                boolean growFactor = utils.getUnfairBoolean(20);
                if (growFactor) grid[x + 1][y] = new GrassParticle(x + 1, y, id);
            }
        }
    }

}
