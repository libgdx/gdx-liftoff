package com.denireaux.fallingsand.particletypes;

/**
 * Abstract base class for all particles in the falling sand simulation.
 *
 * Each particle has an (x, y) position on the grid and a velocity used for simulating motion.
 * Subclasses must implement the {@code update} method to define how the particle behaves over time.
 */
public abstract class Particle {
    public int x, y;
    public float velocity = 0f;

    public Particle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void update(float gravity, Particle[][] grid);

    public Particle[] getSurroundingParticles(Particle[][] grid) {
        Particle left = (x > 0) ? grid[x - 1][y] : null;
        Particle right = (x < grid.length - 1) ? grid[x + 1][y] : null;
        Particle above = (y < grid[0].length - 1) ? grid[x][y + 1] : null;
        Particle below = (y > 0) ? grid[x][y - 1] : null;
    
        return new Particle[]{ left, right, above, below };
    }    
}
