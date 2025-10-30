package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.behaviors.ISolid;

public class WetSoilParticle extends Particle implements ISolid {

    public WetSoilParticle(int x, int y, String id) {
        super(x, y, id);
        this.id = "wetsoil";
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
    
}
