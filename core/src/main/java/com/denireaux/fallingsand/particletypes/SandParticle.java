package com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.utils.utils;

public class SandParticle extends Particle {
    public SandParticle(int x, int y) {
        super(x, y);
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        // velocity += gravity;
        int nextY = y - 1;

        int leftSpace = x - 1;
        int rightSpace = x + 1;

        Particle[] surroundingParticles = this.getSurroundingParticles(grid);
        Particle particleLeft = surroundingParticles[0];
        Particle particleRight = surroundingParticles[1];
        Particle particleAbove = surroundingParticles[2];
        Particle particleBelow = surroundingParticles[3];

        boolean bool = utils.getRandomBoolean();
        System.out.println(bool);

        if (nextY < 0 || nextY >= grid[0].length) return;

        if (particleBelow == null) { moveDown(grid, x, nextY); }

        if (particleBelow != null) { 
            if (bool == true) { moveRight(grid, x + 1, y); }
            moveLeft(grid, x -1, y);
        }

        if (grid[x][nextY] == null) {
            grid[x][nextY] = this;
            grid[x][y] = null;
            y = nextY;
        }

        return;
    }

    public void moveDown(Particle[][] grid, int newX, int newY) {
        int gridHeight = grid[0].length;
        if (newY >= gridHeight || newY <= 0) return;
    
        if (grid[x][y + 1] == null) {
            grid[x][y + 1] = this;
            grid[x][y] = null;
            y++;
        }
    }

    public void moveRight(Particle[][] grid, int newX, int newY) {
        int gridHeight = grid[0].length;

        if (newY >= gridHeight) return;

        if (grid[x + 1][y] == null) {
            grid[x + 1][y] = this;
            grid[x][y] = null;
            y++;
        } else {
            return;
        }
    }

    public void moveLeft(Particle[][] grid, int newX, int newY) {
        int gridHeight = grid[0].length;

        if (newY >= gridHeight) return;

        if (grid[x - 1][y] == null) {
            grid[x - 1][y] = this;
            grid[x][y] = null;
            y++;
        } else {
            return;
        }
    }
}
