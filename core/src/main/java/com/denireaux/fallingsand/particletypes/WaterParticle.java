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

    // @Override
    // public void tryNormalMovement(Particle[][] grid) {
    //     boolean canDown      = MovementHelper.canMoveDown(grid, x, y);
    //     boolean canLeft      = MovementHelper.canLeft(grid, x, y);
    //     boolean canRight     = MovementHelper.canRight(grid, x, y);
    //     boolean canDownLeft  = MovementHelper.canMoveDownLeft(grid, x, y);
    //     boolean canDownRight = MovementHelper.canMoveDownRight(grid, x, y);

    //     if (canDown) {
    //         moveDown(grid);
    //         return;
    //     }

    //     if (canLeft && canRight) {
    //         boolean leftFactor = utils.getRandomBoolean();

    //         if (leftFactor == true) {
    //             moveLeft(grid);
    //             return;
    //         }
    //         moveRight(grid);
    //         return;

    //     } else if (canRight) {
    //         moveRight(grid);
    //         // return;
    //     } else if (canLeft) {
    //         moveLeft(grid);
    //         // return;
    //     }

    //     // if (canDownLeft && canDownRight) {
    //     //     boolean leftFactor = utils.getRandomBoolean();
    //     //     if (leftFactor) {
    //     //         moveDownLeft(grid);
    //     //     } else {
    //     //         moveDownRight(grid);
    //     //     }
    //     //     return;
    //     // } else if (canDownLeft) {
    //     //     moveDownLeft(grid);
    //     //     return;
    //     // } else if (canDownRight) {
    //     //     moveDownRight(grid);
    //     //     return;
    //     // }
    // }

    @Override
    public void tryNormalMovement(Particle[][] grid) {

        boolean canDown      = MovementHelper.canMoveDown(grid, x, y);
        boolean canDownLeft  = MovementHelper.canMoveDownLeft(grid, x, y);
        boolean canDownRight = MovementHelper.canMoveDownRight(grid, x, y);
        boolean canLeft      = MovementHelper.canLeft(grid, x, y);
        boolean canRight     = MovementHelper.canRight(grid, x, y);

        // -------------------------
        // 1. Straight down (gravity)
        // -------------------------
        if (canDown) {
            moveDown(grid);
            return;
        }

        // -------------------------
        // 2. Diagonal spreading
        // -------------------------
        if (canDownLeft && canDownRight) {
            // Random choice for symmetrical flow
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

        // -------------------------
        // 3. Horizontal flow
        // -------------------------
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

        // -------------------------
        // 4. No movement possible
        // -------------------------
        // do nothing
    }


    private void tryMoveRight(Particle[][] grid) {
        Particle[] surroundings = getSurroundingParticles(grid);
        if (surroundings[1] != null) return;
        moveRight(grid);
    }

    private void tryMoveLeft(Particle[][] grid) {
        Particle[] surroundings = getSurroundingParticles(grid);
        if (surroundings[0] != null) return;
        moveLeft(grid);
    }

    private void tryKeepMoving(Particle[][] grid) {
        while (utils.getRandomBoolean()) {
            boolean canLeft  = MovementHelper.canLeft(grid, x, y);
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

            // random stop so it doesn't slide forever
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

    private void evaporate(Particle[][] grid, int x, int y) {
        BoolInt result = checkForHotNeighbors(grid);
        Particle[] surroundings = getSurroundingParticles(grid);
        Particle particle = surroundings[result.indexOfHotParticle];
        if (particle == null) return;
        int particleX = particle.x;
        int particleY = particle.y;

        if (result.canEvaporate()) {
            boolean carbonFactor = utils.getRandomBoolean();
            if (carbonFactor) {
                convertParticle(grid, particleX, particleY, "smoke");
                convertParticle(grid, x, y, "carbon");
                return;
            }
            convertParticle(grid, particleX, particleY, "vapor");
            grid[x][y] = new AshParticle(x, y, "ash");
        }
    }

    // Note -> TODO: Interaction with hot stone needs to be fixed
    private void checkAboveForLessDenseParticle(Particle[][] grid, int x, int y) {
        Particle particleAbove = getAboveParticle(grid, x, y);
        if (particleAbove == null || "stone".equals(particleAbove.getId())) return;

        if ("stone-hot".equals(particleAbove.getId())) {
            evaporate(grid, x, y);
            return;
        }

        if ("water".equals(particleAbove.getId())) return;

        boolean willSinkInWater = particleAbove.willSink;
        if (willSinkInWater) {
            particleAbove.sinkCounter++;
            swapWith(grid, x, y + 1);
        }
    }

    // Note -> TODO: Interaction with hot stone needs to be fixewd
    private void checkBelowForLessDenseParticle(Particle[][] grid, int x, int y) {
        Particle particleBelow = getBelowParticle(grid, x, y);
        if (particleBelow == null || "stone".equals(particleBelow.getId())) return;
        if ("water".equals(particleBelow.getId())) return;

        if ("stone-hot".equals(particleBelow.getId())) {
            evaporate(grid, x, y);
            return;
        }

        boolean willSinkInWater = particleBelow.willSink;
        if (!willSinkInWater) {
            particleBelow.sinkCounter--;
            // assuming swapWith swaps this water with the BELOW cell
            // (if below is (x, y+1), you might want y + 1 here depending on your swapWith impl)
            swapWith(grid, x, y - 1);
        }
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
        evaporate(grid, x, y);
        tryNormalMovement(grid);
        checkAboveForLessDenseParticle(grid, x, y);
        checkBelowForLessDenseParticle(grid, x, y);
    }

}
