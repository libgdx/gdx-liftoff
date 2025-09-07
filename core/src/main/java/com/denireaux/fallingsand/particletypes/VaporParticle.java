package com.denireaux.fallingsand.particletypes;

import java.util.logging.Logger;

import com.denireaux.fallingsand.FallingSandGame;
import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;

public class VaporParticle extends Particle {

    private static final Logger log = Logger.getLogger(String.valueOf(FallingSandGame.class));

    public VaporParticle(int x, int y, String id) {
        super(x, y, id);
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 8;
        velocity -= gravity;
        float maxVelocity = 1.5f;
        velocity = Math.max(velocity, -maxVelocity);

        int moveSteps = (int) Math.abs(velocity);
        if (moveSteps == 0) return;

        for (int i = 0; i < moveSteps; i++) {
            checkInbounds(grid, x, y);
            tryNormalMovementUpwards(grid);
            checkForCondense(grid, x, y);
            condense(grid, x, y);


            // We need logic that make the VaporParticle condense and "rain" at a certain altitude
                // condense()
                    // checkCondensation()

        }

        velocity += moveSteps;
    }

    private void tryNormalMovementUpwards(Particle[][] grid) {
        boolean canMoveUp = MovementHelper.canMoveUp(grid, x, y);
        boolean canLeft = MovementHelper.canLeft(grid, x, y);
        boolean canRight = MovementHelper.canRight(grid, x, y);
        boolean leftFactor = utils.getRandomBoolean();

        Particle[] surroundingParticles = getSurroundingParticles(grid);
        if (surroundingParticles[2] != null) {
            String aboveParticle = surroundingParticles[2].getId();
            if ("water".equals(aboveParticle)) {
                swapWith(grid, x, y - 1);
            }
        }

        if (canMoveUp) {
            moveUp(grid);
            return;
        }

        if (canLeft && canRight) {
            if (leftFactor) {
                moveLeft(grid);
                return;
            }
            moveRight(grid);
        }
    }

    // TODO: Check this if you ever change the window height/weidth
    private boolean checkForCondense(Particle[][] grid, int x, int y) { return y >= 600; }

    private void condense(Particle[][] grid, int x, int y) {
        boolean canCondense = utils.getRandomBoolean();
        if (checkForCondense(grid, x, y)) {

            // Flip a coin
            boolean willCondense = utils.getUnfairBoolean(20);

            // If Heads...
            if (canCondense && willCondense) {
                grid[x][y] = null;
                grid[x][y] = new WaterParticle(x, y, "water");
            }
            
        }
    }







}
