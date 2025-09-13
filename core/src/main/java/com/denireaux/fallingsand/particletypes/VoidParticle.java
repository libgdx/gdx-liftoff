package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.behaviors.ISolid;
import com.denireaux.fallingsand.utils.utils;

public class VoidParticle extends Particle implements ISolid {
    private int sinkCounter = 0;
    private static final float SINK_DELAY = 0.75f;
    private int wetStep = 0;
    private boolean hasDried = false;

    public VoidParticle(int x, int y, String id) {
        super(x, y, id);
        this.willSink = true;
        this.isWet = false;
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 8;
        velocity += gravity;
        float maxVelocity = 1.3f;
        if (velocity > maxVelocity) velocity = maxVelocity;
        float dy = velocity;
        int moveSteps = (int) dy;
        if (moveSteps == 0) return;
        for (int i = 0; i < moveSteps; i++) {
            moveAsSolid(grid, x, y);
        }
    }

    @Override
    public void moveAsSolid(Particle[][] grid, int x, int y) {
        checkInbounds(grid, x, y);
        handleDrySandNeighbors(grid, x, y);
        trySinking(grid, x, y);
        tryNormalMovement(grid);
        // tryContinueToSink(grid, x, y);
        tryDrySelf(grid, x, y);
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

    private void handleDrySandNeighbors(Particle[][] grid, int x, int y) {
        Particle[] surroundings = getSurroundingParticles(grid);
        for (Particle particle : surroundings) {
            if (particle == null) return;
            if (!particle.isWet && "sand".equals(particle.getId())) {
                boolean wetSandFactor = utils.getRandomBoolean();
                boolean canKeepWetting = wetStep >= 10;
                if (wetSandFactor && canKeepWetting) {    
                    int sandParticleX = particle.x; 
                    int sandParticleY = particle.y;    
                    convertParticle(grid, sandParticleX, sandParticleY, "void");
                }
            }
        }
    }

    private void tryDrySelf(Particle[][] grid, int x, int y) {
        if (!hasDried) {
            wetStep += 1;
            if (wetStep < 500) return;
        }
        convertParticle(grid, x, y, "void");
    }
}
