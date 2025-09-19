package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.utils.utils;

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
        if (velocity > maxVelocity) {
            velocity = maxVelocity;
        }
    
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
        Particle invokerParticle;
        for (int index = 0; index < surroundings.length; index++) {
            invokerParticle = surroundings[index];
            if (surroundings[index] == null) return;
            if (surroundings[index].isHot) {
                if (surroundings[index].isCombustable || surroundings[index].hasCombusted) convertParticle(grid, x, y, "smoke");
                convertParticle(grid, x, y, "smoke");
                this.hasCombusted = true;
                grid[x][y].isHot = true;
                // tryMakeAsh(grid);
            }
        }

        // Particle[] surroundings = getSurroundingParticles(grid);

        // for (Particle particle : surroundings) {
        //     if (particle == null) return;
        //     String particleInQuestionid = particle.getId();
        //     if ("powder".equals(particleInQuestionid) && particle.hasCombusted == true) {
        //         int particleInQuestionX = particle.x;
        //         int particleInQuestionY = particle.y;
        //         convertParticle(grid, particleInQuestionX, particleInQuestionY, "smoke");
        //         grid[x][y].hasCombusted = true;
        //     }
        // }
    }

    private void tryMakeAsh(Particle[][] grid) {
        boolean ashFactor = utils.getUnfairBoolean(50);
        if (ashFactor) convertParticle(grid, x, y, "ash");
        grid[x][y] = new AshParticle(x, y, "ash");
    }
}
