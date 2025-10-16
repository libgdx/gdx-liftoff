package com.denireaux.fallingsand.particletypes;

public class LavaParticle extends Particle {
    private int sinkCounter = 0;
    private static final float SINK_DELAY = 0.75f;

    public LavaParticle(int x, int y, String id) {
        super(x, y, id);
        this.isHot = true;
        this.willSink = true;
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 9;
        velocity += gravity;
        velocity = Math.min(velocity, 1.5f);

        int steps = (int) velocity;
        if (steps == 0) return;

        for (int i = 0; i < steps; i++) {
            if (y <= 0) return;
            checkInbounds(grid, x, y);
            tryNormalMovement(grid);
            trySinking(grid, x, y);
        }
        velocity -= steps;
    }

    @Override
    public void trySinking(Particle[][] grid, int x, int y) {
        Particle particleBelow = getSurroundingParticles(grid)[3];
        if (particleBelow == null) return;

        if (sinkCounter >= SINK_DELAY) {
            if ("water".equals(particleBelow.getId())) swapWith(grid, x, y - 1);
            if ("ash".equals(particleBelow.getId())) swapWith(grid, x, y - 1);
            sinkCounter = 0;
            
        } else sinkCounter++;
        
    }

}
