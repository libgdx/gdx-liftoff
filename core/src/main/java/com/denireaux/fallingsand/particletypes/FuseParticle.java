package com.denireaux.fallingsand.particletypes;

public class FuseParticle extends Particle {

    public FuseParticle(int x, int y, String id) {
        super(x, y, id);
        this.isStatic = true;
        this.hasCombusted = false;
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        handleImmolation(grid, x, y);
    }

    private void handleImmolation(Particle[][] grid, int x, int y) {
        Particle[] surroundingParticles = getSurroundingParticles(grid);

        for (Particle particle : surroundingParticles) {
            if (particle == null) return;

            if (particle.isHot) {
                if (particle.isCombustable || particle.hasCombusted) convertParticle(grid, x, y, "smoke");
                this.hasCombusted = true;
                grid[x][y].isHot = true;
            }
            continue;
        }
    }
}
