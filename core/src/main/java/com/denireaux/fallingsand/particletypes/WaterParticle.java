package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;

public class WaterParticle extends Particle {

    public WaterParticle(int x, int y, String id) {
        super(x, y, id);
    }

    protected final String id = "water";

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 9;
        velocity += gravity;
        float maxVelocity = 1.3f;
        velocity = Math.min(velocity, maxVelocity);

        int moveSteps = (int) velocity;
        if (moveSteps == 0) return;

        for (int i = 0; i < moveSteps; i++) {
            checkInbounds(grid, x, y);
            evaporate(grid, x, y);
            tryNormalMovement(grid);

        }

        velocity -= moveSteps;
    }

    private void moveTo(Particle[][] grid, int destinationsX, int destinatinsY) {
        grid[x][y] = null;
        x = destinationsX;
        y = destinatinsY;
        grid[x][y] = this;
    }

    @Override
    public void tryNormalMovement(Particle[][] grid) {
        boolean canDown = MovementHelper.canMoveDown(grid, x, y);
        boolean canLeft = MovementHelper.canLeft(grid, x, y);
        boolean canRight = MovementHelper.canRight(grid, x, y);
        boolean canDownLeft = MovementHelper.canMoveDownLeft(grid, x, y);
        boolean canDownRight = MovementHelper.canMoveDownRight(grid, x, y);
        boolean leftFactor = utils.getRandomBoolean();

        if (canDown) {
            moveDown(grid);
            return;
        }

        if (canDownLeft && canDownRight) {
            if (leftFactor) {
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
            if (leftFactor) {
                moveLeft(grid);
                flattenOut(grid, -1);
                tryKeepMoving(grid);
                flattenOut(grid, -1);
            } else {
                moveRight(grid);
                flattenOut(grid, 1);
                tryKeepMoving(grid);
                flattenOut(grid, 1);
            }
            return;
        }

        if (canLeft) {
            moveLeft(grid);
            flattenOut(grid, -1);
            tryKeepMoving(grid);
            flattenOut(grid, -1);
            return;
        }

        if (canRight) {
            moveRight(grid);
            flattenOut(grid, 1);
            tryKeepMoving(grid);
            flattenOut(grid, 1);
            return;
        }
    }

    public void tryMoveRight(Particle[][] grid) {
        Particle[] surroundings = getSurroundingParticles(grid);
        if (surroundings[1] == null) return;
        moveRight(grid);
    }

    public void tryMoveLeft(Particle[][] grid) {
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
            int newX = x + dir;
            if (dir < 0 && MovementHelper.canLeft(grid, x, y)) {
                moveLeft(grid);
            } else if (dir > 0 && MovementHelper.canRight(grid, x, y)) {
                moveRight(grid);
            } else {
                break;
            }
            steps++;
            if (Math.random() < 0.3) break;
        }
    }

    public static record BoolInt(boolean canEvaporate, int indexOfHotParticle) {}

    public BoolInt checkForHotNeighbors(Particle[][] grid) {
        Particle[] surroundings = getSurroundingParticles(grid);

        for (int i = 0; i < surroundings.length; i++) {
            if (surroundings[i] != null && surroundings[i].isHot) {
                return new BoolInt(true, i);
            }
        }

        return new BoolInt(false, 1);
    }

    private void evaporate(Particle[][] grid, int x, int y) {
        BoolInt result = checkForHotNeighbors(grid);

        Particle[] surroundings = getSurroundingParticles(grid);
        Particle particle = surroundings[result.indexOfHotParticle];

        if (particle == null) return;

        int particleX = particle.x;
        int particleY = particle.y;

        if (result.canEvaporate()) {
            convertParticle(grid,particleX, particleY, "vapor");
            grid[x][y] = new AshParticle(x, y, "ash");
        }
    }

}
