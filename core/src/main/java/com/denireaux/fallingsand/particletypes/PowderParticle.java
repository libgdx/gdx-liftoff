package com.denireaux.fallingsand.particletypes;

public class PowderParticle extends Particle {

    public PowderParticle(int x, int y, String id) {
        super(x, y, id);
        this.id = "powder";
        this.willSink = false;
        this.isCombustable = true;
        this.hasCombusted = false;
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
            tryExplode(grid);
            tryNormalMovement(grid);
        }
        velocity -= moveSteps;
    }

    private void tryExplode(Particle[][] grid) {
        Particle[] surroundings = getSurroundingParticles(grid);
        for (Particle surroundingParticle : surroundings) {
            if (surroundingParticle == null) return;
            if (surroundingParticle.isHot) {
                if (surroundingParticle.isCombustable || surroundingParticle.hasCombusted) convertParticle(grid, x, y, "smoke");
                this.hasCombusted = true;
                grid[x][y].isHot = true;
            }
        }
    }
}
