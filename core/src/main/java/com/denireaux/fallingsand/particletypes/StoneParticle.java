package com.denireaux.fallingsand.particletypes;

public class StoneParticle extends Particle {
    public StoneParticle(int x, int y) {
        super(x, y);
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        // Immobile solid block
    }

    @Override 
    public void moveDown(Particle[][] grid) {
        grid[x][y] = null;
        grid[x][y - 1] = this;
        y--;
    }

    @Override 
    public void moveRight(Particle[][] grid) {
        // Optional: implement later
    }

    @Override 
    public void moveLeft(Particle[][] grid) {
        // Optional: implement later
    }
}
