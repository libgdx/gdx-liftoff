package core.src.main.java.com.denireaux.fallingsand.particletypes;

import com.denireaux.fallingsand.particletypes.Particle;

public class WetSandParticle extends Particle {
    public WetSandParticle(int x, int y) {
        super(x, y);
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override 
    public void moveDown(Particle[][] grid) {
        System.out.println("Not implimented");
    }

    @Override 
    public void moveRight(Particle[][] grid) {
        System.out.println("Not implimented");
    }

    @Override 
    public void moveLeft(Particle[][] grid) {
        System.out.println("Not implimented");
    }

}
