package com.denireaux.fallingsand.particletypes;

public abstract class Particle {
    public int x, y;
    public float velocity = 0f;
    public boolean isHot = false;
    public boolean isCold = false;

    public Particle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void update(float gravity, Particle[][] grid);

    public Particle[] getSurroundingParticles(Particle[][] grid) {
        Particle left = (x > 0) ? grid[x - 1][y] : null;
        Particle right = (x < grid.length - 1) ? grid[x + 1][y] : null;
        Particle above = (y < grid[0].length - 1) ? grid[x][y + 1] : null; // higher y = up
        Particle below = (y > 0) ? grid[x][y - 1] : null; // lower y = down
    
        return new Particle[]{ left, right, above, below };
    }    
}
