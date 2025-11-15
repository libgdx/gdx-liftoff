package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.fauna.WormFactory;
import com.denireaux.fallingsand.utils.utils;

public class GrassParticle extends Particle {

    public GrassParticle(int x, int y, String id) {
        super(x, y, id);
        this.id = "grass";
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        gravity *= 9;
        velocity += gravity;
        float maxVelocity = 1.5f;
        velocity = Math.min(velocity, maxVelocity);
        int moveSteps = (int) velocity;
        if (moveSteps == 0) return;

        for (int i = 0; i < moveSteps; i++) {
            checkInbounds(grid, x, y);
            tryNormalMovement(grid);
        }

        velocity -= moveSteps;
    }

    @Override
    public void tryNormalMovement(Particle[][] grid) {
        tryGrow(grid);
        // TODO: Fix handling of worm spawn/worm behavior
        // Note: Might be best if not a particle type
        // handleWormSpawn(grid, x, y);
    }

    private void tryGrow(Particle[][] grid) {
        Particle[] surroundingParticles = getSurroundingParticles(grid);
        Particle aboveParticle = surroundingParticles[2];
        Particle leftParticle = surroundingParticles[0];
        Particle rightParticle = surroundingParticles[1];

        if (aboveParticle == null) {
            boolean growFactor = utils.getUnfairBoolean(20);
            if (growFactor) grid[x][y + 1] = new GrassParticle(x, y + 1, id);
            return;
        }

        if ("wetsoil".equals(aboveParticle.getId())) {
                boolean transformFactor = utils.getUnfairBoolean(20);
                if (transformFactor) {
                    grid[x][y + 1] = null; 
                    grid[x][y + 1] = new GrassParticle(x, y + 1, "grass");
                    return;
                }
        }

        boolean spreadFactor = utils.getUnfairBoolean(20);

        if (spreadFactor) {
            if (leftParticle == null) {
                boolean growFactor = utils.getUnfairBoolean(20);
                if (growFactor) grid[x - 1][y] = new GrassParticle(x - 1, y, id);
            }

            if (rightParticle == null) {
                boolean growFactor = utils.getUnfairBoolean(20);
                if (growFactor) grid[x + 1][y] = new GrassParticle(x + 1, y, id);
            }
        }
    }

    private void handleWormSpawn(Particle[][] grid, int x, int y) {
        // Two random gates to keep worms rare
        boolean wormFactor          = utils.getUnfairBoolean(20);
        boolean wormDelaySpawnFactor = utils.getUnfairBoolean(20);

        if (!(wormFactor && wormDelaySpawnFactor)) {
            return;
        }

        int width  = grid.length;
        int height = grid[0].length;

        // Let’s spawn the worm with its head just above this grass tile
        int headX = x;
        int headY = y + 1; // or y - 1 depending on how you think "above" works in your grid

        // Make sure the worm fits: head, mid, tail horizontally to the left
        if (!inBounds(headX, headY, width, height)) return;
        if (!inBounds(headX - 1, headY, width, height)) return;
        if (!inBounds(headX - 2, headY, width, height)) return;

        if (grid[headX][headY] != null) return;
        if (grid[headX - 1][headY] != null) return;
        if (grid[headX - 2][headY] != null) return;

        // Correct call: use coordinates, not grid[x][y]
        WormFactory.spawnWorm(headX, headY, grid);
    }

    private boolean inBounds(int x, int y, int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

}
