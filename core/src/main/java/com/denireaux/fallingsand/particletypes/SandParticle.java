package com.denireaux.fallingsand.particletypes;

public class SandParticle extends Particle {
    public SandParticle(int x, int y) {
        super(x, y);
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        velocity += gravity;

        if (y <= 0) return;

        Particle[] particles = getSurroundingParticles(grid);
        Particle particleLeft = particles[0];
        Particle particleRight = particles[1];
        Particle particleBelow = particles[3];

        // Down
        if (y > 0 && grid[x][y - 1] == null) {
            moveDown(grid, x, y - 1);
            return;
        }

        // Down-right
        if (x + 1 < grid.length && y - 1 >= 0 && particleRight == null && grid[x + 1][y - 1] == null) {
            moveDownRight(grid);
            return;
        }

        // Down-left
        if (x - 1 >= 0 && y - 1 >= 0 && particleLeft == null && grid[x - 1][y - 1] == null) {
            moveDownLeft(grid);
            return;
        }
    }

    private void moveDown(Particle[][] grid, int newX, int newY) {
        grid[newX][newY] = this;
        grid[x][y] = null;
        x = newX;
        y = newY;
    }

    private void moveDownRight(Particle[][] grid) {
        grid[x][y] = null;
        grid[x + 1][y - 1] = this;
        x++;
        y--;
    }

    private void moveDownLeft(Particle[][] grid) {
        grid[x][y] = null;
        grid[x - 1][y - 1] = this;
        x--;
        y--;
    }
}
