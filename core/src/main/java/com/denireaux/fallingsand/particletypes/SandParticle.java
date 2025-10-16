package com.denireaux.fallingsand.particletypes;

public class SandParticle extends Particle {
    public SandParticle(int x, int y, String id) {
        super(x, y, id);
        this.willSink = true;
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
            checkInbounds(grid, x, y);
            tryMakeWetSand(grid, x, y);
            tryNormalMovement(grid);
            gravity += 1;
        }
        velocity -= moveSteps;
    }

    private void tryMakeWetSand(Particle[][] grid, int x, int y) {
        Particle[] surroundingParticles = getSurroundingParticles(grid);
        for (Particle particle : surroundingParticles) {
            if (particle == null) continue;
            String particleId = particle.getId();
            if ("water".equals(particleId)) {
                velocity *= 0.25f;
                convertParticle(grid, x, y, "wetsand");
                break;
            }
        }
    }
}
