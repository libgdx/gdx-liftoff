package com.denireaux.fallingsand.particletypes;

public class StoneParticle extends Particle {
    public StoneParticle(int x, int y, String id) {
        super(x, y, id);
        this.isStatic = true;
    }

    @Override
    public void update(float gravity, Particle[][] grid) {}
}
