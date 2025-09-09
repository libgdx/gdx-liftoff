package com.denireaux.fallingsand.particletypes;

import java.util.logging.Logger;

import com.denireaux.fallingsand.behaviors.ISolid;
import com.denireaux.fallingsand.utils.utils;

public class SnowParticle extends Particle implements ISolid {

    private static final Logger log = Logger.getLogger(String.valueOf(SnowParticle.class));

    public int meltStep;

    public SnowParticle(int x, int y, String id) {
        super(x, y, id);
        this.willSink = false;
        this.meltStep = 0;
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 8;
        velocity += gravity;
        float maxVelocity = 1.3f;
        if (velocity > maxVelocity) velocity = maxVelocity;
        float dy = velocity;
        int moveSteps = (int) dy;
        if (moveSteps == 0) return;
        for (int i = 0; i < moveSteps; i++) {
            moveAsSolid(grid, x, y);
        }
        velocity -= moveSteps;
    }

    private void tryMelt(Particle[][] grid) {
        if (meltStep >= 1000) {
            boolean meltFactor = utils.getRandomBoolean();
            if (meltFactor) convertParticle(grid, x, y, "water");
        }
        Particle[] surroundings = getSurroundingParticles(grid);
        for (Particle particle : surroundings) {
            if (particle == null) return;
            if (particle.isHot) meltStep += 250;
        }
    }

    @Override
    public void moveAsSolid(Particle[][] grid, int x, int y) {
        checkInbounds(grid, x, y);
        tryNormalMovement(grid);
        tryMelt(grid);
        meltStep++;
    }

}
