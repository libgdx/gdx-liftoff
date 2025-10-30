package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.behaviors.ISolid;

public class SoilParticle extends Particle implements ISolid {
    public SoilParticle(int x, int y, String id) {
        super(x, y, id);
        this.id = "soil";
        this.isWet = false;
    }
    
    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 9;
        velocity += gravity;
        float maxVelocity = 1.5f;
        if (velocity > maxVelocity) velocity = maxVelocity;
        float dy = velocity;
        int moveSteps = (int) dy;
        if (moveSteps == 0) return;
        for (int i = 0; i < moveSteps; i++) {
            moveAsSolid(grid, x, y);
            gravity += 1;
        }
        velocity -= moveSteps;
    }

    @Override
    public void moveAsSolid(Particle[][] grid, int x, int y) {
        checkInbounds(grid, x, y);
        checkForWetness(grid, x, y);
        trySinking(grid, x, y);
        tryNormalMovement(grid);
        // tryContinueToSink(grid, x, y);
    }

    private void tryContinueToSink(Particle[][] grid, int x, int y) {
        Particle particleLeft = getLeftParticle(grid, x, y);
        Particle particleRight = getRightParticle(grid, x, y);
        if (particleLeft == null || particleRight == null) return;
        String particleLeftId = particleLeft.getId();
        String particleRightId = particleRight.getId();
        if ("water".equals(particleLeftId) && "water".equals(particleRightId)) {
            if (Math.random() < 0.5) {
                trySwappingWithRight(grid, x, y);
                deleteSelf(grid, x, y);
            } else {
                trySwappingWithLeft(grid, x, y);
                deleteSelf(grid, x, y);
            }
            return;
        }
        trySwappingWithLeft(grid, x, y);
    }

    private void checkForWetness(Particle[][] grid, int x, int y) {
        Particle[] surroundingParticles = getSurroundingParticles(grid);
        for (Particle particle : surroundingParticles) {
            if (particle == null) continue;
            if ("water".equals(particle.getId())) this.isWet = true;
        }
    }
}
