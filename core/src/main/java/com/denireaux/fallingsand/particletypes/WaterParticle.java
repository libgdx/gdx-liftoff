package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.behaviors.ILiquid;
import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;

public class WaterParticle extends Particle implements ILiquid {

    public WaterParticle(int x, int y, String id) {
        super(x, y, id);
        this.id = "water";
        this.isWet = true;
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 9;
        velocity += gravity;
        float maxVelocity = 1.6f;
        velocity = Math.min(velocity, maxVelocity);
        int moveSteps = (int) velocity;
        if (moveSteps == 0) return;

        for (int i = 0; i < moveSteps; i++) {
            moveAsLiquid(grid, x, y);
        }

        velocity -= moveSteps;
    }

    @Override
    public void tryNormalMovement(Particle[][] grid) {
        boolean canDown      = MovementHelper.canMoveDown(grid, x, y);
        boolean canDownLeft  = MovementHelper.canMoveDownLeft(grid, x, y);
        boolean canDownRight = MovementHelper.canMoveDownRight(grid, x, y);
        boolean canLeft      = MovementHelper.canLeft(grid, x, y);
        boolean canRight     = MovementHelper.canRight(grid, x, y);

        if (canDown) {
            moveDown(grid);
            return;
        }

        if (canDownLeft && canDownRight) {
            if (utils.getRandomBoolean()) {
                moveDownLeft(grid);
            } else {
                moveDownRight(grid);
            }
            return;
        }

        if (canDownLeft) {
            moveDownLeft(grid);
            return;
        }

        if (canDownRight) {
            moveDownRight(grid);
            return;
        }

        if (canLeft && canRight) {
            if (utils.getRandomBoolean()) {
                moveLeft(grid);
            } else {
                moveRight(grid);
            }
            return;
        }

        if (canLeft) {
            moveLeft(grid);
            return;
        }

        if (canRight) {
            moveRight(grid);
            return;
        }

    }

    public static record BoolInt(boolean canEvaporate, int indexOfHotParticle) {}
    private BoolInt checkForHotNeighbors(Particle[][] grid) {
        Particle[] surroundings = getSurroundingParticles(grid);
        for (int i = 0; i < surroundings.length; i++) {
            if (surroundings[i] != null && surroundings[i].isHot) {
                return new BoolInt(true, i);
            }
        }
        return new BoolInt(false, 1);
    }

    private void evaporate(Particle[][] grid, int x, int y, boolean isAshless) {
        BoolInt result = checkForHotNeighbors(grid);
        if (!result.canEvaporate()) return;

        Particle[] surroundings = getSurroundingParticles(grid);
        Particle particle = surroundings[result.indexOfHotParticle];
        if (particle == null) return;

        int particleX = particle.x;
        int particleY = particle.y;

        if (isAshless) {
            grid[x][y] = new VaporParticle(x, y, "vapor");
            grid[particleX][particleY] = null;
        }

        boolean carbonFactor = utils.getRandomBoolean();
        if (carbonFactor) {
            convertParticle(grid, particleX, particleY, "smoke");
            convertParticle(grid, x, y, "carbon");
            grid[x][y].isHot = true;
            return;
        }
        convertParticle(grid, particleX, particleY, "vapor");
        grid[x][y] = new AshParticle(x, y, "ash");
    }

    private void checkAboveForLessDenseParticle(Particle[][] grid, int x, int y) {
        Particle particleAbove = getAboveParticle(grid, x, y);
        if (particleAbove == null || "stone".equals(particleAbove.getId())) return;
        if ("stone-hot".equals(particleAbove.getId())) {
            evaporate(grid, x, y, false);
            return;
        }
        if ("water".equals(particleAbove.getId())) return;
        boolean willSinkInWater = particleAbove.willSink;
        if (willSinkInWater) {
            particleAbove.sinkCounter++;
            swapWith(grid, x, y + 1);
        }
    }

    private void checkBelowForLessDenseParticle(Particle[][] grid, int x, int y) {
        Particle particleBelow = getBelowParticle(grid, x, y);
        if (particleBelow == null || "vapor".equals(particleBelow.getId())) return;
        if ("lava".equals(particleBelow.getId())) evaporate(grid, x, y, false);
        if ("water".equals(particleBelow.getId())) return;
        if ("stone-hot".equals(particleBelow.getId())) {
            evaporate(grid, x, y, true);
            return;
        }

        boolean willSinkInWater = particleBelow.willSink;
        if (!willSinkInWater) {
            particleBelow.sinkCounter--;
            swapWith(grid, x, y - 1);
        }
    }

    @Override
    public void moveAsLiquid(Particle[][] grid, int x, int y) {
        checkInbounds(grid, x, y);
        evaporate(grid, x, y, false);
        tryNormalMovement(grid);
        checkAboveForLessDenseParticle(grid, x, y);
        checkBelowForLessDenseParticle(grid, x, y);
    }
}
