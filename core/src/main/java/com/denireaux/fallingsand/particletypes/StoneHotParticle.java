package com.denireaux.fallingsand.particletypes;

public class StoneHotParticle extends Particle {

    public StoneHotParticle(int x, int y, String id) {
        super(x, y, id);
        this.isStatic = true;
        this.isHot = true;
    }

    @Override
    public void update(float gravity, Particle[][] grid) {}
}
