package com.denireaux.fallingsand.particletypes;

public class AshParticle extends Particle {

    public AshParticle(int x, int y, String id) {
        super(x, y, id);
        this.willSink = true;
    }

    private int sinkCounter = 0;
    private static final float SINK_DELAY = 0.75f;

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
            checkInbounds(grid, x, y);
            tryNormalMovement(grid);
            trySinking(grid, x, y);
        }
    
        velocity -= moveSteps;
    }

    public void trySinking(Particle[][] grid, int x, int y) {
        Particle particleBelow = getSurroundingParticles(grid)[3];
        if (particleBelow == null) return;

        if (sinkCounter >= SINK_DELAY) {
            if ("water".equals(particleBelow.getId())) swapWith(grid, x, y - 1);
            sinkCounter = 0;
        } else sinkCounter++;
        
    }
    
}
