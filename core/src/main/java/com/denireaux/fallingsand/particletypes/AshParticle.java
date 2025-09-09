package com.denireaux.fallingsand.particletypes;

public class AshParticle extends Particle {
    public int sinkCounter = 0;
    private static final float SINK_DELAY = 0.75f;

    public AshParticle(int x, int y, String id) {
        super(x, y, id);
        this.willSink = false;
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 9;
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
        }
        velocity -= moveSteps;
    }

}
