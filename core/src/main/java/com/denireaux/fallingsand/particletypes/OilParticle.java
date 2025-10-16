package com.denireaux.fallingsand.particletypes;

import java.util.logging.Logger;

import com.denireaux.fallingsand.behaviors.ILiquid;
import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;

public class OilParticle extends Particle implements ILiquid {

    private static final Logger log = Logger.getLogger(String.valueOf(OilParticle.class));

    public OilParticle(int x, int y, String id) {
        super(x, y, id);
        this.willSink = false;
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
            moveAsLiquid(grid, x, y);
        }
        velocity -= moveSteps;
    }

    @Override
    public void tryNormalMovement(Particle[][] grid) {
        boolean canDown = MovementHelper.canMoveDown(grid, x, y);
        boolean canLeft = MovementHelper.canLeft(grid, x, y);
        boolean canRight = MovementHelper.canRight(grid, x, y);
        boolean canDownLeft = MovementHelper.canMoveDownLeft(grid, x, y);
        boolean canDownRight = MovementHelper.canMoveDownRight(grid, x, y);
        if (canDown) {
            moveDown(grid);
            return;
        }
        if (canDownLeft && canDownRight) {
            boolean leftFactor = utils.getRandomBoolean();
            if (leftFactor) {
                moveDownLeft(grid);
            } else {
                moveDownRight(grid);
            }
            return;
        }
        if (canDownLeft) moveDownLeft(grid);
        if (canDownRight) moveDownRight(grid);
        if (canLeft && canRight) {
            boolean leftFactor = utils.getRandomBoolean();
            if (leftFactor) leftMovement(grid, x, y);
            rightMovement(grid, x, y);
            return;
        }
        if (canLeft) leftMovement(grid, x, y);
        if (canRight) rightMovement(grid, x, y);
    }

    private void leftMovement(Particle[][] grid, int x, int y) {
        tryMoveLeft(grid);
        flattenOut(grid, -1);
        tryKeepMoving(grid);
        flattenOut(grid, -1);
    }

    private void rightMovement(Particle[][]grid, int x, int y) {
        tryMoveRight(grid);
        flattenOut(grid, 1);
        tryKeepMoving(grid);
        flattenOut(grid, 1);
    }

    @Override
    public void moveAsLiquid(Particle[][] grid, int x, int y) {
        checkInbounds(grid, x, y);
        tryIgniteSelf(grid, x, y);
        tryNormalMovement(grid);
    }

    private void tryMoveRight(Particle[][] grid) {
        Particle[] surroundings = getSurroundingParticles(grid);
        if (surroundings[1] == null) return;
        moveRight(grid);
    }

    private void tryMoveLeft(Particle[][] grid) {
        Particle[] surroundings = getSurroundingParticles(grid);
        if (surroundings[0] == null) return;
        moveLeft(grid);
    }

    private void tryKeepMoving(Particle[][] grid) {
        while (utils.getRandomBoolean()) {
            boolean canLeft = MovementHelper.canLeft(grid, x, y);
            boolean canRight = MovementHelper.canRight(grid, x, y);
            if (canLeft && !canRight) {
                moveLeft(grid);
            } else if (canRight && !canLeft) {
                moveRight(grid);
            } else if (canLeft && canRight) {
                if (utils.getRandomBoolean()) {
                    moveLeft(grid);
                } else {
                    moveRight(grid);
                }
            } else {
                break;
            }
            if (Math.random() < 0.3) break;
        }
    }

    private void flattenOut(Particle[][] grid, int dir) {
        int steps = 0;
        while (steps < 3) {
            if (dir < 0 && MovementHelper.canLeft(grid, x, y)) {
                moveLeft(grid);
            } else if (dir > 0 && MovementHelper.canRight(grid, x, y)) {
                moveRight(grid);
            } else break;
            steps++;
            if (Math.random() < 0.3) break;
        }
    }

    private void tryIgniteSelf(Particle[][] grid, int x, int y) {
        Particle[] surroundings = getSurroundingParticles(grid);
        for (Particle particle : surroundings) {
            if (particle == null) return;
            if (particle.isHot) {
                boolean carbonFactor = utils.getRandomBoolean();
                boolean fastFactor = utils.getRandomBoolean();
                if (carbonFactor && fastFactor) {
                    grid[x][y].isHot = true;
                    convertParticle(grid, particle.x, particle.y, "smoke");
                    grid[particle.x][particle.y].isHot = true;
                }
            }
        }
    }
}
