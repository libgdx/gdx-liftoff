package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.behaviors.ISolid;


public class CarbonParticle extends Particle implements ISolid {

    public int sinkCounter = 0;
    private static final float SINK_DELAY = 0.75f;
    protected final String id = "carbon";

    public CarbonParticle(int x, int y, String id) {
        super(x, y, id);
        this.willSink = true;
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 9;
        velocity += gravity;
        float maxVelocity = 1.5f;
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
        tryNormalMovement(grid);
    }
}
