package com.denireaux.fallingsand.particletypes;

import java.util.HashMap;
import java.util.function.BiFunction;

public class SandParticle extends Particle {
    public SandParticle(int x, int y, String id) {
        super(x, y, id);
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
            checkInbounds(grid, x, y);
            tryMakeWetSand(grid, x, y);
            tryNormalMovement(grid);
        }
    
        velocity -= moveSteps;
    }

    private void tryMakeWetSand(Particle[][] grid, int x, int y) {
        Particle[] surroundingParticles = getSurroundingParticles(grid);
        if (surroundingParticles[3] == null) return;
        String belowParticleId = surroundingParticles[3].getId();
        if ("water".equals(belowParticleId)) {
            convertParticle(grid, x, y, "wetsand");
        }
    }

}
