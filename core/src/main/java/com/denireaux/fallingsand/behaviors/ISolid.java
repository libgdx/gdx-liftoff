package com.denireaux.fallingsand.behaviors;

import com.denireaux.fallingsand.particletypes.Particle;

public interface ISolid {
    void moveAsSolid(Particle[][] grid, int x, int y);
}
