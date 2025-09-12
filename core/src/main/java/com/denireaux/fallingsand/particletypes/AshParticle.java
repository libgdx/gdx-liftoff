package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.behaviors.ISolid;

public class AshParticle extends Particle implements ISolid {
    public AshParticle(int x, int y, String id) {
        super(x, y, id);
        this.willSink = true;
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 8;
        velocity += gravity;
        float maxVelocity = 1.3f;
        
        if (velocity > maxVelocity) {
            velocity = maxVelocity;
        }

        float dy = velocity;
        int moveSteps = (int) dy;

        if (moveSteps == 0) return;
        for (int i = 0; i < moveSteps; i++) {
            moveAsSolid(grid, x, y);
        }

        velocity -= moveSteps;
    }

    @Override
    public void moveAsSolid(Particle[][] grid, int x, int y) {
        checkInbounds(grid, x, y);
        trySinking(grid, x, y);
        tryNormalMovement(grid);
    }

}
